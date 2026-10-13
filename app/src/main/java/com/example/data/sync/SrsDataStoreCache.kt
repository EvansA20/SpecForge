package com.example.data.sync

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.DocumentSection
import com.example.data.model.Requirement
import com.example.data.model.SrsDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "specforge_srs_cache")

class SrsDataStoreCache(private val context: Context) {

    companion object {
        private val KEY_LAST_SYNC = longPreferencesKey("key_last_sync_time")
        private val KEY_DOCUMENTS_CACHE = stringPreferencesKey("key_documents_cache_json")
        private val KEY_SECTIONS_CACHE = stringPreferencesKey("key_sections_cache_json")
        private val KEY_REQUIREMENTS_CACHE = stringPreferencesKey("key_requirements_cache_json")
        private val KEY_PENDING_MUTATIONS = stringPreferencesKey("key_pending_mutations_json")
        private val KEY_OFFLINE_SIMULATION = booleanPreferencesKey("key_offline_simulation")
    }

    val lastSyncTimeFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_LAST_SYNC] ?: 0L
    }

    val pendingMutationsCountFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        val json = preferences[KEY_PENDING_MUTATIONS] ?: "[]"
        try {
            JSONArray(json).length()
        } catch (e: Exception) {
            0
        }
    }

    val cachedDocumentsFlow: Flow<List<SrsDocument>> = context.dataStore.data.map { preferences ->
        val json = preferences[KEY_DOCUMENTS_CACHE] ?: "[]"
        deserializeDocuments(json)
    }

    suspend fun getCachedSections(documentId: String): List<DocumentSection> {
        var result = emptyList<DocumentSection>()
        context.dataStore.edit { preferences ->
            val json = preferences[KEY_SECTIONS_CACHE] ?: "{}"
            result = deserializeSectionsForDoc(json, documentId)
        }
        return result
    }

    suspend fun getCachedRequirements(documentId: String): List<Requirement> {
        var result = emptyList<Requirement>()
        context.dataStore.edit { preferences ->
            val json = preferences[KEY_REQUIREMENTS_CACHE] ?: "{}"
            result = deserializeRequirementsForDoc(json, documentId)
        }
        return result
    }

    /**
     * Cache documents, sections, and requirements into DataStore Preferences
     */
    suspend fun cacheSnapshot(
        documents: List<SrsDocument>,
        sectionsMap: Map<String, List<DocumentSection>>,
        requirementsMap: Map<String, List<Requirement>>
    ) {
        val docsJson = serializeDocuments(documents)
        val sectionsJson = serializeSectionsMap(sectionsMap)
        val reqsJson = serializeRequirementsMap(requirementsMap)

        context.dataStore.edit { preferences ->
            preferences[KEY_DOCUMENTS_CACHE] = docsJson
            preferences[KEY_SECTIONS_CACHE] = sectionsJson
            preferences[KEY_REQUIREMENTS_CACHE] = reqsJson
            preferences[KEY_LAST_SYNC] = System.currentTimeMillis()
        }
    }

    /**
     * Queue a mutation made while offline into DataStore Preferences
     */
    suspend fun queuePendingMutation(type: String, entityId: String, payloadJson: String) {
        context.dataStore.edit { preferences ->
            val currentQueue = preferences[KEY_PENDING_MUTATIONS] ?: "[]"
            val array = try {
                JSONArray(currentQueue)
            } catch (e: Exception) {
                JSONArray()
            }

            val mutationObj = JSONObject().apply {
                put("id", java.util.UUID.randomUUID().toString())
                put("type", type)
                put("entityId", entityId)
                put("payload", payloadJson)
                put("timestamp", System.currentTimeMillis())
            }

            array.put(mutationObj)
            preferences[KEY_PENDING_MUTATIONS] = array.toString()
        }
    }

    /**
     * Get and clear all pending mutations once connection is restored
     */
    suspend fun flushPendingMutations(): List<PendingMutation> {
        var result = emptyList<PendingMutation>()
        context.dataStore.edit { preferences ->
            val currentQueue = preferences[KEY_PENDING_MUTATIONS] ?: "[]"
            val array = try {
                JSONArray(currentQueue)
            } catch (e: Exception) {
                JSONArray()
            }

            val list = mutableListOf<PendingMutation>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    PendingMutation(
                        id = obj.getString("id"),
                        type = obj.getString("type"),
                        entityId = obj.getString("entityId"),
                        payload = obj.getString("payload"),
                        timestamp = obj.getLong("timestamp")
                    )
                )
            }
            result = list
            // Clear pending mutations
            preferences[KEY_PENDING_MUTATIONS] = "[]"
            preferences[KEY_LAST_SYNC] = System.currentTimeMillis()
        }
        return result
    }

    // --- JSON Serialization Helpers ---
    private fun serializeDocuments(documents: List<SrsDocument>): String {
        val array = JSONArray()
        for (doc in documents) {
            val obj = JSONObject().apply {
                put("id", doc.id)
                put("projectId", doc.projectId)
                put("title", doc.title)
                put("version", doc.version)
                put("status", doc.status)
                put("author", doc.author)
                put("organization", doc.organization)
                put("overview", doc.overview)
                put("confidentiality", doc.confidentiality)
                put("lastModified", doc.lastModified)
            }
            array.put(obj)
        }
        return array.toString()
    }

    private fun deserializeDocuments(json: String): List<SrsDocument> {
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<SrsDocument>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    SrsDocument(
                        id = obj.getString("id"),
                        projectId = obj.getString("projectId"),
                        title = obj.getString("title"),
                        version = obj.optString("version", "1.0.0"),
                        status = obj.optString("status", "Draft"),
                        author = obj.optString("author", "Lead Architect"),
                        organization = obj.optString("organization", "Engineering Dept"),
                        overview = obj.optString("overview", ""),
                        confidentiality = obj.optString("confidentiality", "Confidential"),
                        lastModified = obj.optLong("lastModified", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun serializeSectionsMap(sectionsMap: Map<String, List<DocumentSection>>): String {
        val root = JSONObject()
        for ((docId, sections) in sectionsMap) {
            val arr = JSONArray()
            for (s in sections) {
                val sObj = JSONObject().apply {
                    put("id", s.id)
                    put("documentId", s.documentId)
                    put("sectionNumber", s.sectionNumber)
                    put("title", s.title)
                    put("content", s.content)
                    put("orderIndex", s.orderIndex)
                    put("isEnabled", s.isEnabled)
                    put("guidancePrompt", s.guidancePrompt)
                }
                arr.put(sObj)
            }
            root.put(docId, arr)
        }
        return root.toString()
    }

    private fun deserializeSectionsForDoc(json: String, docId: String): List<DocumentSection> {
        return try {
            val root = JSONObject(json)
            if (!root.has(docId)) return emptyList()
            val arr = root.getJSONArray(docId)
            val list = mutableListOf<DocumentSection>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    DocumentSection(
                        id = obj.getString("id"),
                        documentId = obj.getString("documentId"),
                        sectionNumber = obj.getString("sectionNumber"),
                        title = obj.getString("title"),
                        content = obj.getString("content"),
                        orderIndex = obj.getInt("orderIndex"),
                        isEnabled = obj.optBoolean("isEnabled", true),
                        guidancePrompt = obj.optString("guidancePrompt", "")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun serializeRequirementsMap(reqMap: Map<String, List<Requirement>>): String {
        val root = JSONObject()
        for ((docId, reqs) in reqMap) {
            val arr = JSONArray()
            for (r in reqs) {
                val rObj = JSONObject().apply {
                    put("id", r.id)
                    put("documentId", r.documentId)
                    put("reqId", r.reqId)
                    put("title", r.title)
                    put("statement", r.statement)
                    put("type", r.type)
                    put("priority", r.priority)
                    put("status", r.status)
                    put("acceptanceCriteria", r.acceptanceCriteria)
                    put("verificationMethod", r.verificationMethod)
                    put("linkedUseCases", r.linkedUseCases)
                    put("orderIndex", r.orderIndex)
                }
                arr.put(rObj)
            }
            root.put(docId, arr)
        }
        return root.toString()
    }

    private fun deserializeRequirementsForDoc(json: String, docId: String): List<Requirement> {
        return try {
            val root = JSONObject(json)
            if (!root.has(docId)) return emptyList()
            val arr = root.getJSONArray(docId)
            val list = mutableListOf<Requirement>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    Requirement(
                        id = obj.getString("id"),
                        documentId = obj.getString("documentId"),
                        reqId = obj.getString("reqId"),
                        title = obj.getString("title"),
                        statement = obj.getString("statement"),
                        type = obj.optString("type", "Functional"),
                        priority = obj.optString("priority", "Must Have"),
                        status = obj.optString("status", "Draft"),
                        acceptanceCriteria = obj.optString("acceptanceCriteria", ""),
                        verificationMethod = obj.optString("verificationMethod", "Test"),
                        linkedUseCases = obj.optString("linkedUseCases", ""),
                        orderIndex = obj.optInt("orderIndex", 0)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }
}

data class PendingMutation(
    val id: String,
    val type: String,
    val entityId: String,
    val payload: String,
    val timestamp: Long
)

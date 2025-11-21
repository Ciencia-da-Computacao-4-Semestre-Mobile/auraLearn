package com.eliel.studytrack.data.firestore

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp

object StudyPlanRepository {
    @SuppressLint("StaticFieldLeak")
    private val db = Firebase.firestore
    private const val COLLECTION =   "study_plans"

    private fun userId(): String? = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun addPlan(plan: StudyPlan): String {
        val uid = plan.userId.ifBlank { userId() ?: "" }
        val docRef = db.collection(COLLECTION).document()
        val planToSave = plan.copy(
            id = docRef.id,
            userId = uid,
            createdAt = Timestamp.now()
        )
        docRef.set(planToSave).await()
        return docRef.id
    }

    suspend fun updatePlan(plan: StudyPlan) {
        if (plan.id.isBlank()) return
        db.collection(COLLECTION).document(plan.id).set(plan).await()
    }

    suspend fun deletePlan(planId: String) {
        db.collection(COLLECTION).document(planId).delete().await()
    }

suspend fun getPlansForCurrentUser(): List<StudyPlan> {
    val uid = userId() ?: return emptyList()
    val snapshot = db.collection(COLLECTION)
        .whereEqualTo("userId", uid)
        .get().await()
    
    
    val plans = snapshot.documents.mapNotNull { it.toObject(StudyPlan::class.java) }
    return plans.sortedByDescending { it.createdAt }
}

    suspend fun getPlan(planId: String): StudyPlan? {
        val doc = db.collection(COLLECTION).document(planId).get().await()
        return doc.toObject(StudyPlan::class.java)
    }
}

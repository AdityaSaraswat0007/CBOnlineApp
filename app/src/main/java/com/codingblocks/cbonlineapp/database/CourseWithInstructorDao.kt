package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.database.models.CourseRunPair
import com.codingblocks.cbonlineapp.database.models.CourseWithInstructor
import com.codingblocks.cbonlineapp.database.models.InstructorModel

@Dao
interface CourseWithInstructorDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(join: CourseWithInstructor)

    @Query("""
        SELECT i.* FROM InstructorModel i
        INNER JOIN coursewithinstructor ON
        i.uid = coursewithinstructor.instructor_id
        WHERE coursewithinstructor.course_id = :courseID
        """)
    fun getInstructorWithCourseId(courseID: String): LiveData<List<InstructorModel>>

    @Query("""
        SELECT i.* FROM InstructorModel i
        INNER JOIN coursewithinstructor ON
        i.uid = coursewithinstructor.instructor_id
        WHERE coursewithinstructor.course_id = :courseID
        """)
    suspend fun getInstructors(courseID: String): List<InstructorModel>

    @Transaction
    @Query("""
       SELECT c.*,r.* FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE r.crAttemptId IS NULL
    """)
    fun getCourses(): LiveData<List<CourseInstructorPair>>

    @Transaction
    @Query("""
       SELECT c.*,r.* FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE r.crCourseId IN (:courses) AND r.crAttemptId IS NULL 
    """)
    fun getJobCourses(courses: ArrayList<String>): LiveData<List<CourseInstructorPair>>

    @Transaction
    @Query("""
       SELECT c.*,r.* FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE r.crCourseId = :courseId AND r.crAttemptId IS NULL 
    """)
    fun getCourse(courseId: String): LiveData<CourseInstructorPair>

    @Transaction
    @Query("""
        SELECT c.*,r.* FROM RunModel r
	    INNER JOIN CourseModel c ON c.cid = r.crCourseId
        WHERE r.crAttemptId IS NULL AND recommended = 1
            """)
    fun getRecommendedCourses(): LiveData<List<CourseInstructorPair>>

    @Transaction
    @Query("""
    SELECT c.*,r.* FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE r.crAttemptId IS NOT NULL ORDER BY lastAccessed DESC,crEnrollmentEnd DESC
    """)
    fun getMyRuns(): LiveData<List<CourseInstructorPair>>

    @Transaction
    @Query("""
    SELECT c.*,r.* FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE r.crAttemptId IS NOT NULL ORDER BY lastAccessed DESC,crEnrollmentEnd DESC LIMIT 3
    """)
    fun getRecentRuns(): LiveData<List<CourseInstructorPair>>

    @Transaction
    @Query("""
    SELECT c.*,r.* FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE r.crAttemptId IS NOT NULL AND r.crRunAttemptEnd < :currentTimeMillis ORDER BY r.lastAccessed DESC,crEnrollmentEnd 
    """)
    fun getExpiredRuns(currentTimeMillis: Long): LiveData<List<CourseInstructorPair>>

    @Query("""
        SELECT * FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE r.crAttemptId IS NOT NULL ORDER BY lastAccessed DESC LIMIT 2
    """)
    fun getTopRuns(): LiveData<List<CourseRunPair>>

    @Query("""
        SELECT * FROM RunModel r
	   INNER JOIN CourseModel c ON c.cid = r.crCourseId
       WHERE r.crAttemptId IS NOT NULL ORDER BY lastAccessed DESC LIMIT 1
    """)
    fun getTopRun(): LiveData<CourseRunPair>
}

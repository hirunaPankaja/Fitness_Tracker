package com.example.gym_workout

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.database.DatabaseHelperWorkout
import com.example.gym_workout.databinding.YogaActivityBinding

class YogaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelperWorkout
    private lateinit var binding: YogaActivityBinding
    private val completedWorkouts = mutableListOf<String>() // Track completed workouts
    private var totalCaloriesBurned = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = YogaActivityBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        dbHelper = DatabaseHelperWorkout(this)

        // Insert data for all yoga poses
        insertYogaPoseData()

        // Set click listeners for the yoga pose images
        binding.imgChildPose.setOnClickListener { onCardClick(it) }
        binding.imgDownwardDog.setOnClickListener { onCardClick(it) }
        binding.imgCatCowPose.setOnClickListener { onCardClick(it) }
        binding.imgWarriorIIPose.setOnClickListener { onCardClick(it) }
        binding.imgCobraPose.setOnClickListener { onCardClick(it) }
        binding.imgSeatedForwardBend.setOnClickListener { onCardClick(it) }
        binding.imgBridgePose.setOnClickListener { onCardClick(it) }
        binding.imgCorpsePose.setOnClickListener { onCardClick(it) }

        binding.btnStartSession.setOnClickListener { onStartWorkoutClick(it) }
        binding.btnBack.setOnClickListener { onBackClick(it) }

        // Check for completed workouts
        val completedArray = intent.getStringArrayExtra("completedWorkouts") ?: arrayOf()
        completedWorkouts.addAll(completedArray.asList())

        // Check completed workouts and update UI
        checkCompletedWorkouts()
    }

    private fun onBackClick(view: View) {
        finish()
    }

    private fun insertYogaPoseData() {
        // Yoga Pose 1: Child's Pose
        val childPoseImage = BitmapFactory.decodeResource(resources, R.drawable.childs_pose)
        dbHelper.insertExercise(
            "ChildPose",
            "Child's Pose (Balasana)",
            "1. Start on your hands and knees.\n2. Spread your knees wide while keeping your toes touching.\n3. Sit your hips back onto your heels and stretch your arms forward on the mat.\n4. Rest your forehead on the floor and relax your body.\n5. Breathe deeply and stay in this position.",
            "Hold for 30–60 seconds",
            childPoseImage
        )

        // Yoga Pose 2: Downward Dog
        val downwardDogImage = BitmapFactory.decodeResource(resources, R.drawable.downward_dog)
        dbHelper.insertExercise(
            "DownwardDog",
            "Downward Dog (Adho Mukha Svanasana)",
            "1. Start in a tabletop position on your hands and knees.\n2. Tuck your toes under and lift your hips up towards the ceiling.\n3. Keep your arms straight, with your head between your arms.\n4. Aim to straighten your legs, but a slight bend in the knees is okay.\n5. Press your heels toward the floor and hold the position.",
            "Hold for 30–60 seconds",
            downwardDogImage
        )

        // Yoga Pose 3: Cat-Cow Pose
        val catCowPoseImage = BitmapFactory.decodeResource(resources, R.drawable.cat_cow_pose)
        dbHelper.insertExercise(
            "CatCowPose",
            "Cat-Cow Pose (Marjaryasana-Bitilasana)",
            "1. Start in a tabletop position.\n2. On an inhale, arch your back, drop your belly toward the floor, and lift your head and tailbone (Cow Pose).\n3. On an exhale, round your back, tuck your chin toward your chest, and pull your belly in (Cat Pose).\n4. Flow between these two poses with your breath.",
            "Perform for 8–10 cycles (inhale/exhale)",
            catCowPoseImage
        )

        // Yoga Pose 4: Warrior II
        val warriorIIPoseImage = BitmapFactory.decodeResource(resources, R.drawable.warrior_ii_pose)
        dbHelper.insertExercise(
            "WarriorIIPose",
            "Warrior II (Virabhadrasana II)",
            "1. Stand with your feet wide apart.\n2. Turn your right foot out 90 degrees and your left foot slightly inward.\n3. Bend your right knee, keeping it directly over your ankle.\n4. Extend your arms parallel to the floor, with palms facing down.\n5. Hold the pose and switch sides.",
            "Hold for 30 seconds on each side",
            warriorIIPoseImage
        )

        // Yoga Pose 5: Cobra Pose
        val cobraPoseImage = BitmapFactory.decodeResource(resources, R.drawable.cobra_pose)
        dbHelper.insertExercise(
            "CobraPose",
            "Cobra Pose (Bhujangasana)",
            "1. Lie face down on your mat with your hands under your shoulders.\n2. Press your palms into the mat and lift your chest off the ground.\n3. Keep your elbows slightly bent and shoulders relaxed.\n4. Engage your lower back muscles and hold.",
            "Hold for 30–45 seconds",
            cobraPoseImage
        )

        // Yoga Pose 6: Seated Forward Bend
        val seatedForwardBendImage = BitmapFactory.decodeResource(resources, R.drawable.seated_forward_bend)
        dbHelper.insertExercise(
            "SeatedForwardBend",
            "Seated Forward Bend (Paschimottanasana)",
            "1. Sit with your legs extended straight out in front of you.\n2. Inhale, lengthen your spine, and reach your arms up.\n3. Exhale and hinge forward from your hips, reaching for your feet.\n4. Keep your back straight as you fold forward and hold.",
            "Hold for 30–60 seconds",
            seatedForwardBendImage
        )

        // Yoga Pose 7: Bridge Pose
        val bridgePoseImage = BitmapFactory.decodeResource(resources, R.drawable.bridge_pose)
        dbHelper.insertExercise(
            "BridgePose",
            "Bridge Pose (Setu Bandhasana)",
            "1. Lie on your back with your knees bent and feet hip-width apart.\n2. Place your arms alongside your body, palms facing down.\n3. Press through your feet and lift your hips toward the ceiling.\n4. Squeeze your glutes and hold the position.\n5. Lower slowly and repeat if desired.",
            "Hold for 30–45 seconds",
            bridgePoseImage
        )

        // Yoga Pose 8: Corpse Pose
        val corpsePoseImage = BitmapFactory.decodeResource(resources, R.drawable.corpse_pose)
        dbHelper.insertExercise(
            "CorpsePose",
            "Corpse Pose (Savasana)",
            "1. Lie flat on your back with your arms at your sides, palms facing up.\n2. Let your feet fall naturally outward.\n3. Close your eyes, relax your entire body, and focus on your breath.\n4. Stay in this position to calm your mind and body.",
            "Hold for 1 min",
            corpsePoseImage
        )
    }


    private fun checkCompletedWorkouts() {
        // Iterate over completed workouts and update UI
        if (completedWorkouts.contains("ChildPose")) {
            binding.root.findViewById<ImageView>(R.id.imgChildPose).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("DownwardDog")) {
            binding.root.findViewById<ImageView>(R.id.imgDownwardDog).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("CatCowPose")) {
            binding.root.findViewById<ImageView>(R.id.imgCatCowPose).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("WarriorIIPose")) {
            binding.root.findViewById<ImageView>(R.id.imgWarriorIIPose).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("CobraPose")) {
            binding.root.findViewById<ImageView>(R.id.imgCobraPose).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("SeatedForwardBend")) {
            binding.root.findViewById<ImageView>(R.id.imgSeatedForwardBend).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("BridgePose")) {
            binding.root.findViewById<ImageView>(R.id.imgBridgePose).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("CorpsePose")) {
            binding.root.findViewById<ImageView>(R.id.imgCorpsePose).visibility = View.VISIBLE
        }

        // Check if all workouts are completed
        if (completedWorkouts.containsAll(listOf("ChildPose", "DownwardDog", "CatCowPose", "WarriorIIPose", "CobraPose", "SeatedForwardBend", "BridgePose", "CorpsePose"))) {
            Log.d("YogaActivity", "All workouts completed. Showing popup.")
            showCompletionPopup()
        } else {
            Log.d("YogaActivity", "Workouts not completed yet.")
        }
    }

    private fun showCompletionPopup() {
        val popupDialog = Popupworkoutcomplete(this, totalCaloriesBurned,totalCaloriesBurned)
        popupDialog.setOnDismissListener {
            // Return to the main screen when the popup is dismissed
            val intent = Intent(this, YogaActivity::class.java)
            startActivity(intent)
        }
        popupDialog.show()
    }

    fun onCardClick(view: View) {
        Log.d("YogaActivity", "Card clicked: ${view.id}")

        val exerciseName = when (view.id) {
            R.id.imgChildPose -> "ChildPose"
            R.id.imgDownwardDog -> "DownwardDog"
            R.id.imgCatCowPose -> "CatCowPose"
            R.id.imgWarriorIIPose -> "WarriorIIPose"
            R.id.imgCobraPose -> "CobraPose"
            R.id.imgSeatedForwardBend -> "SeatedForwardBend"
            R.id.imgBridgePose -> "BridgePose"
            R.id.imgCorpsePose -> "CorpsePose"
            else -> {
                Log.e("YogaActivity", "Unknown view ID: ${view.id}")
                return
            }
        }

        Log.d("YogaActivity", "Exercise: $exerciseName")
        fetchExerciseDetails(exerciseName)
    }

    private fun fetchExerciseDetails(exerciseName: String) {
        val exercise = dbHelper.getExercise(exerciseName)
        if (exercise != null) {
            val intent = Intent(this, Workout::class.java)
            intent.putExtra("title", exercise.title)
            intent.putExtra("instructions", exercise.instructions)
            intent.putExtra("repsSets", exercise.repsSets)
            intent.putExtra("exerciseName", exercise.name)
            intent.putExtra("completedWorkouts", completedWorkouts.toTypedArray())
            intent.putExtra("totalCaloriesBurned", totalCaloriesBurned) // Pass total calories burned
            startActivityForResult(intent, REQUEST_CODE)
        } else {
            Toast.makeText(this, "No such exercise found", Toast.LENGTH_SHORT).show()
        }
    }

    fun onStartWorkoutClick(view: View) {
        Log.d("YogaActivity", "Start Workout button clicked")
        Toast.makeText(this, "Workout Started!", Toast.LENGTH_SHORT).show()

        // Define the list of exercises for the session
        val exercises = listOf("ChildPose", "DownwardDog", "CatCowPose", "WarriorIIPose", "CobraPose", "SeatedForwardBend", "BridgePose", "CorpsePose")

        // Start the first exercise
        startExercise(exercises, 0)
    }

    private fun startExercise(exercises: List<String>, currentIndex: Int) {
        if (currentIndex < exercises.size) {
            val exercise = dbHelper.getExercise(exercises[currentIndex])
            if (exercise != null) {
                totalCaloriesBurned += getCaloriesBurned(exercise.name)

                val intent = Intent(this, Workout::class.java)
                intent.putExtra("title", exercise.title)
                intent.putExtra("instructions", exercise.instructions)
                intent.putExtra("repsSets", exercise.repsSets)
                intent.putExtra("exerciseName", exercise.name)
                intent.putExtra("exercises", exercises.toTypedArray())
                intent.putExtra("currentIndex", currentIndex)
                intent.putExtra("completedWorkouts", completedWorkouts.toTypedArray())
                intent.putExtra("totalCaloriesBurned", totalCaloriesBurned) // Pass total calories burned
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                Toast.makeText(this, "No such exercise found", Toast.LENGTH_SHORT).show()
            }
        } else {
            // All workouts completed, show completion popup
            Log.d("YogaActivity", "All exercises completed. Showing popup.")
            showCompletionPopup()
        }
    }

    private fun getCaloriesBurned(exerciseName: String): Int {
        return when (exerciseName) {
            "ChildPose" -> 10
            "DownwardDog" -> 15
            "CatCowPose" -> 10
            "WarriorIIPose" -> 20
            "CobraPose" -> 15
            "SeatedForwardBend" -> 10
            "BridgePose" -> 15
            "CorpsePose" -> 5
            else -> 0
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val completedArray = data?.getStringArrayExtra("completedWorkouts") ?: arrayOf()
            completedWorkouts.addAll(completedArray.asList())

            val currentIndex = data?.getIntExtra("currentIndex", -1) ?: -1
            val exercises = data?.getStringArrayExtra("exercises")?.toList() ?: emptyList()
            if (currentIndex != -1) {
                startExercise(exercises, currentIndex + 1)
            } else {
                checkCompletedWorkouts()
            }
        }
    }


    companion object {
        private const val REQUEST_CODE = 1
    }
}

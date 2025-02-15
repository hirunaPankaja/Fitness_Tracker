package com.example.gym_workout.utils

class MealPlanGenerator {

    companion object {
        fun getMealPlan(dietPlan: String): Map<String, List<String>> {
            return when (dietPlan.lowercase()) {
                "vegetarian" -> getVegetarianMealPlan()
                "non vegetarian" -> getNonVegetarianMealPlan()
                "vegan" -> getVeganMealPlan()
                else -> getDefaultMealPlan()
            }
        }

        fun getVegetarianMealPlan(): Map<String, List<String>> {
            return mapOf(
                "breakfast" to listOf(
                    "String hoppers with coconut sambol",
                    "Pol roti with banana",
                    "Milk rice with lunu miris",
                    "Dhal curry with roti",
                    "Pittu with coconut milk",
                    "Vegetable kottu",
                    "Fruit salad with yogurt"
                ),
                "lunch" to listOf(
                    "Rice with dhal, beetroot curry, and papadum",
                    "Vegetable fried rice",
                    "Jackfruit curry with rice",
                    "Pumpkin curry with roti",
                    "Brinjal moju with rice",
                    "Cashew curry with rice",
                    "Vegetable biryani"
                ),
                "dinner" to listOf(
                    "Vegetable noodles",
                    "Potato curry with roti",
                    "Mushroom curry with rice",
                    "Spinach curry with rice",
                    "Vegetable stir-fry with rice",
                    "Lentil curry with rice",
                    "Vegetable kottu"
                )
            )
        }

        fun getNonVegetarianMealPlan(): Map<String, List<String>> {
            return mapOf(
                "breakfast" to listOf(
                    "Egg hoppers with sambol",
                    "Fish buns with tea",
                    "Chicken roti",
                    "Egg sandwich",
                    "Kiri bath with fish curry",
                    "Chicken kottu",
                    "Egg curry with roti"
                ),
                "lunch" to listOf(
                    "Rice with chicken curry, dhal, and papadum",
                    "Fish ambul thiyal with rice",
                    "Prawn curry with rice",
                    "Chicken fried rice",
                    "Beef curry with rice",
                    "Crab curry with rice",
                    "Eggplant and chicken curry with rice"
                ),
                "dinner" to listOf(
                    "Chicken kottu",
                    "Fish curry with rice",
                    "Prawn fried rice",
                    "Beef stir-fry with noodles",
                    "Chicken biryani",
                    "Egg curry with roti",
                    "Fish cutlets with salad"
                )
            )
        }

        fun getVeganMealPlan(): Map<String, List<String>> {
            return mapOf(
                "breakfast" to listOf(
                    "Coconut roti with sambol",
                    "Fruit salad with coconut milk",
                    "Pittu with coconut milk",
                    "String hoppers with coconut sambol",
                    "Pol roti with banana",
                    "Milk rice with lunu miris (vegan)",
                    "Vegetable kottu"
                ),
                "lunch" to listOf(
                    "Rice with dhal, beetroot curry, and papadum",
                    "Jackfruit curry with rice",
                    "Pumpkin curry with roti",
                    "Brinjal moju with rice",
                    "Cashew curry with rice",
                    "Vegetable biryani",
                    "Lentil curry with rice"
                ),
                "dinner" to listOf(
                    "Vegetable noodles",
                    "Potato curry with roti",
                    "Mushroom curry with rice",
                    "Spinach curry with rice",
                    "Vegetable stir-fry with rice",
                    "Lentil curry with rice",
                    "Vegetable kottu"
                )
            )
        }

        fun getDefaultMealPlan(): Map<String, List<String>> {
            return mapOf(
                "breakfast" to listOf("Default breakfast"),
                "lunch" to listOf("Default lunch"),
                "dinner" to listOf("Default dinner")
            )
        }

        // Function to get nutrient and calorie information for a meal
        fun getNutrientsForMeal(mealName: String): String {
            return when (mealName) {
                // Vegetarian Meals
                "String hoppers with coconut sambol" -> "Calories: 300 | Protein: 8g | Carbs: 50g | Fat: 10g"
                "Pol roti with banana" -> "Calories: 250 | Protein: 5g | Carbs: 40g | Fat: 8g"
                "Milk rice with lunu miris" -> "Calories: 350 | Protein: 10g | Carbs: 60g | Fat: 12g"
                "Dhal curry with roti" -> "Calories: 400 | Protein: 12g | Carbs: 55g | Fat: 15g"
                "Pittu with coconut milk" -> "Calories: 320 | Protein: 7g | Carbs: 45g | Fat: 10g"
                "Vegetable kottu" -> "Calories: 450 | Protein: 15g | Carbs: 60g | Fat: 18g"
                "Fruit salad with yogurt" -> "Calories: 200 | Protein: 6g | Carbs: 30g | Fat: 5g"

                // Non-Vegetarian Meals
                "Egg hoppers with sambol" -> "Calories: 350 | Protein: 15g | Carbs: 40g | Fat: 12g"
                "Fish buns with tea" -> "Calories: 300 | Protein: 12g | Carbs: 35g | Fat: 10g"
                "Chicken roti" -> "Calories: 400 | Protein: 20g | Carbs: 45g | Fat: 15g"
                "Egg sandwich" -> "Calories: 300 | Protein: 14g | Carbs: 30g | Fat: 12g"
                "Kiri bath with fish curry" -> "Calories: 500 | Protein: 25g | Carbs: 60g | Fat: 20g"
                "Chicken kottu" -> "Calories: 550 | Protein: 30g | Carbs: 65g | Fat: 25g"
                "Egg curry with roti" -> "Calories: 450 | Protein: 18g | Carbs: 50g | Fat: 18g"

                // Vegan Meals
                "Coconut roti with sambol" -> "Calories: 300 | Protein: 6g | Carbs: 40g | Fat: 10g"
                "Fruit salad with coconut milk" -> "Calories: 200 | Protein: 4g | Carbs: 30g | Fat: 6g"
                "Pittu with coconut milk" -> "Calories: 320 | Protein: 7g | Carbs: 45g | Fat: 10g"
                "String hoppers with coconut sambol" -> "Calories: 300 | Protein: 8g | Carbs: 50g | Fat: 10g"
                "Pol roti with banana" -> "Calories: 250 | Protein: 5g | Carbs: 40g | Fat: 8g"
                "Milk rice with lunu miris (vegan)" -> "Calories: 350 | Protein: 10g | Carbs: 60g | Fat: 12g"
                "Vegetable kottu" -> "Calories: 450 | Protein: 15g | Carbs: 60g | Fat: 18g"

                // Default Meals
                else -> "Calories: 200 | Protein: 10g | Carbs: 30g | Fat: 5g"
            }
        }
    }
}
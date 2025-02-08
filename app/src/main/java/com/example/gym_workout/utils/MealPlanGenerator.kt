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

        private fun getVegetarianMealPlan(): Map<String, List<String>> {
            return mapOf(
                "breakfast" to listOf(
                    "Oatmeal with fruits",
                    "Avocado toast",
                    "Smoothie bowl",
                    "Pancakes with maple syrup",
                    "Scrambled tofu",
                    "Yogurt with granola",
                    "Veggie omelette"
                ),
                "lunch" to listOf(
                    "Quinoa salad",
                    "Lentil soup",
                    "Veggie wrap",
                    "Stuffed bell peppers",
                    "Pasta primavera",
                    "Chickpea curry",
                    "Vegetable stir-fry"
                ),
                "dinner" to listOf(
                    "Grilled veggie skewers",
                    "Spinach and ricotta pasta",
                    "Sweet potato and black bean tacos",
                    "Eggplant parmesan",
                    "Veggie burger",
                    "Mushroom risotto",
                    "Stuffed zucchini boats"
                )
            )
        }

        private fun getNonVegetarianMealPlan(): Map<String, List<String>> {
            return mapOf(
                "breakfast" to listOf(
                    "Scrambled eggs with bacon",
                    "Chicken sausage and toast",
                    "Protein smoothie",
                    "Breakfast burrito",
                    "Pancakes with eggs",
                    "Greek yogurt with honey",
                    "Omelette with ham"
                ),
                "lunch" to listOf(
                    "Grilled chicken salad",
                    "Beef stir-fry",
                    "Turkey sandwich",
                    "Salmon and quinoa",
                    "Chicken wrap",
                    "Steak and veggies",
                    "Tuna salad"
                ),
                "dinner" to listOf(
                    "Grilled salmon",
                    "Chicken Alfredo pasta",
                    "Beef tacos",
                    "Shrimp scampi",
                    "BBQ ribs",
                    "Roast chicken",
                    "Lamb chops"
                )
            )
        }

        private fun getVeganMealPlan(): Map<String, List<String>> {
            return mapOf(
                "breakfast" to listOf(
                    "Chia pudding",
                    "Vegan pancakes",
                    "Smoothie bowl",
                    "Avocado toast",
                    "Tofu scramble",
                    "Oatmeal with almond milk",
                    "Vegan muffins"
                ),
                "lunch" to listOf(
                    "Buddha bowl",
                    "Vegan sushi",
                    "Lentil salad",
                    "Vegan chili",
                    "Quinoa and black bean salad",
                    "Vegan curry",
                    "Stuffed bell peppers"
                ),
                "dinner" to listOf(
                    "Vegan lasagna",
                    "Stir-fried tofu",
                    "Vegan pizza",
                    "Jackfruit tacos",
                    "Vegan shepherd's pie",
                    "Mushroom risotto",
                    "Vegan sushi rolls"
                )
            )
        }

        private fun getDefaultMealPlan(): Map<String, List<String>> {
            return mapOf(
                "breakfast" to listOf("Default breakfast"),
                "lunch" to listOf("Default lunch"),
                "dinner" to listOf("Default dinner")
            )
        }
    }
}
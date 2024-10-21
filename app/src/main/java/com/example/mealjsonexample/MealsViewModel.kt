package com.example.mealjsonexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealsViewModel: ViewModel() {
    private var mealsRepository = MealsRepository()
    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var categories = MutableStateFlow(mealsRepository.categoriesState)
    val categoriesState = categories.asStateFlow()

    private var meals = MutableStateFlow(mealsRepository.mealsState)
    val mealsState = meals.asStateFlow()

    private var meal = MutableStateFlow(mealsRepository.mealState)
    val mealState = meal.asStateFlow()

    private var _chosenCategoryName = MutableStateFlow("")

    private var _chosenMealName = MutableStateFlow("")


    init {
        getAllCategories()
    }

    fun setChosenCategory(name: String){
        _chosenCategoryName.value = name
    }

    fun setChosenMeal(mealName: String){
        _chosenMealName.value = mealName
    }

    fun getAllDishesByCategoryName(){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                meals.value = meals.value.copy(
                    isLoading = true,
                )
                val response = mealsRepository.getAllMealsByCategoryName(_chosenCategoryName.value)
                meals.value = meals.value.copy(
                    isLoading = false,
                    isError = false,
                    result = response.meals
                )

            }
            catch (e: Exception){
                meals.value = meals.value.copy(
                    isError = true,
                    isLoading = false,
                    error = e.message
                )
            }
            finally {
                 _isLoading.value = false
            }
        }
    }

    private fun getAllCategories(){
        viewModelScope.launch {
            try {
                categories.value = categories.value.copy(
                    isLoading = true
                )
                val response = mealsRepository.getAllCategories()

                categories.value = categories.value.copy(
                    isLoading = false,
                    isError = false,
                    result = response.categories
                )
            }
            catch (e: Exception){
                categories.value = categories.value.copy(
                    isError = true,
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun searchMealByName() {
        viewModelScope.launch {
            try {
                meal.value = meal.value.copy(
                    isLoading = true
                )
                val response = mealsRepository.searchMealByName(_chosenMealName.value)
                meal.value = meal.value.copy(
                    isLoading = false,
                    isError = false,
                    result = response.meals
                )
            } catch (e: Exception) {
                meal.value = meal.value.copy(
                    isError = true,
                    isLoading = false,
                    error = e.message

                )
            }
        }
    }

    fun getDishesByArea(area: String){
        viewModelScope.launch{
            try {
                meals.value = meals.value.copy(
                    isLoading = true
                )
                val response = mealsRepository.getMealsByArea(area)
                meals.value = meals.value.copy(
                    isLoading = false,
                    isError = false,
                    result = response.meals
                )
            }
            catch (e: Exception){
                meals.value = meals.value.copy(
                    isError = true,
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}
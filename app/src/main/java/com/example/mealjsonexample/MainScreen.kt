package com.example.mealjsonexample

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import coil3.compose.AsyncImage

@Composable
fun Navigation(
    modifier: Modifier,
    navigationController: NavHostController
) {
    val viewModel: MealsViewModel = viewModel()
    NavHost(
        modifier = modifier,
        navController = navigationController,
        startDestination = Graph.mainScreen.route
    ) {
        composable(route = Graph.secondScreen.route) {
            SecondScreen(viewModel, navigationController)
        }
        composable(route = Graph.mainScreen.route) {
            MainScreen(viewModel, navigationController)
        }
        composable(route = Graph.dishScreen.route) {
            DishScreen(viewModel)
        }
    }
}


@SuppressLint("RememberReturnType")
@Composable
fun SecondScreen(viewModel: MealsViewModel, navigationController: NavHostController) {
    val dishesState = viewModel.mealsState.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllDishesByCategoryName()
    }

    Column{

        if (isLoading.value){
            LoadingScreen()
        }
        else{
            if (dishesState.value.isError){
                ErrorScreen(dishesState.value.error!!)
            }
            if (dishesState.value.result.isNotEmpty()){
                DishesScreen(dishesState.value.result, viewModel, navigationController)
            }
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun DishesScreen(result: List<Meal>, viewModel: MealsViewModel, navigationController: NavHostController) {

    LaunchedEffect(Unit) {
        viewModel.getAllDishesByCategoryName()
    }

    Spacer(Modifier.height(5.dp))
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(result) { meal ->
            DishItem(meal, navigationController, viewModel)
        }
    }
}


@Composable
fun DishItem(meal: Meal, navigationController: NavHostController, viewModel: MealsViewModel) {
    Box(
        modifier = Modifier
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(12.dp))
            .height(400.dp)
            .padding(3.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
            .border(width = 3.dp, color = Color.Black, shape = RoundedCornerShape(12.dp))
            .clickable {
                viewModel.setChosenMeal(meal.idMeal)
                navigationController.navigate(Graph.dishScreen.route)
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                modifier = Modifier.height(150.dp),
                model = meal.strMealThumb,
                contentDescription = null
            )
            Spacer(Modifier.height(5.dp))
            Text(
                modifier = Modifier
                    .padding(horizontal = 11.dp, vertical = 22.dp)
                    .fillMaxWidth(),
                text = meal.mealName,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun MainScreen(viewModel: MealsViewModel, navigationController: NavHostController){

    val categoriesState = viewModel.categoriesState.collectAsState()

    if (categoriesState.value.isLoading){
        LoadingScreen()
    }
    if (categoriesState.value.isError){
        ErrorScreen(categoriesState.value.error!!)
    }
    if (categoriesState.value.result.isNotEmpty()){
        CategoriesScreen(viewModel, categoriesState.value.result, navigationController)
    }

}

@Composable
fun CategoriesScreen(viewModel: MealsViewModel, result: List<Category>, navigationController: NavHostController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2)
    ) {
        items(result){
            CategoryItem(viewModel, it, navigationController)
        }
    }
}

@Composable
fun CategoryItem(viewModel: MealsViewModel, category: Category, navigationController: NavHostController) {
    Box(
        modifier = Modifier
            .height(200.dp)
            .shadow(elevation = 9.dp, shape = RoundedCornerShape(12.dp), clip = true)
            .padding(2.dp)
            .border(width = 3.dp, color = Color.Black, shape = RoundedCornerShape(12.dp))
            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
            .clickable {
                viewModel.setChosenCategory(category.strCategory)
                navigationController.navigate(Graph.secondScreen.route)
            }
    ){
       Column(
           modifier = Modifier.fillMaxSize(),
           horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.Center
       ) {
           AsyncImage(
               model = category.strCategoryThumb,
               contentDescription = null
           )
           Spacer(Modifier.height(5.dp))
           Text(
               text = category.strCategory
           )
       }
    }
}

@Composable
fun ErrorScreen(error: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error
        )
    }
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun DishScreen(
    viewModel: MealsViewModel
) {
    val mealState = viewModel.mealState.collectAsState()

    remember {
        viewModel.searchMealByName()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (mealState.value.isLoading) {
            LoadingScreen()
        }
        else if (mealState.value.isError) {
            ErrorScreen(mealState.value.error.toString())
        }
        else if (mealState.value.result.isNotEmpty()) {
            AsyncImage(
                model = mealState.value.result[0].strMealThumb,
                contentDescription = mealState.value.result[0].mealName
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = mealState.value.result[0].mealName,
                textAlign = TextAlign.Center
            )
            Text(
                text = mealState.value.result[0].strInstructions ?: "Wait.",
                textAlign = TextAlign.Center
            )
        }
        else {
            Text("No meals found.")
        }
    }
}





//@Composable
//fun DishesByAreaScreen(result: List<Meal>, viewModel: MealsViewModel){
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2)
//    ) {
//
//
//        items(result){
//            DishesScreen(result, viewModel)
//        }
//    }
//}
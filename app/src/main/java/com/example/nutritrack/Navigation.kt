package com.example.nutritrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.nutritrack.data.local.NutriTrackDatabase
import com.example.nutritrack.ui.screens.DashboardScreen
import com.example.nutritrack.ui.screens.SearchScreen
import com.example.nutritrack.ui.viewmodel.DashboardViewModel
import com.example.nutritrack.ui.viewmodel.SearchViewModel

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Dashboard)
  val context = LocalContext.current
  val database = NutriTrackDatabase.getDatabase(context)
  
  val factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
          if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
              return DashboardViewModel(database.dao()) as T
          }
          if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
              return SearchViewModel(database.dao()) as T
          }
          throw IllegalArgumentException("Unknown ViewModel class")
      }
  }

  val dashboardViewModel: DashboardViewModel = viewModel(factory = factory)
  val searchViewModel: SearchViewModel = viewModel(factory = factory)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Dashboard> {
          DashboardScreen(
              viewModel = dashboardViewModel,
              onNavigateToSearch = { backStack.add(Search) }
          )
        }
        entry<Search> {
          SearchScreen(
              viewModel = searchViewModel,
              onNavigateBack = { backStack.removeLastOrNull() }
          )
        }
      },
  )
}

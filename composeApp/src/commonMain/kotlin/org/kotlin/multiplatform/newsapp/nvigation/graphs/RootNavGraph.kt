package org.kotlin.multiplatform.newsapp.nvigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.kotlin.multiplatform.newsapp.nvigation.Graph

@Composable
fun RootNavGraph(
    rootNavController: NavHostController,
    innerPadding: PaddingValues,
    navController: NavController
) {

    NavHost(
        navController = rootNavController,
        startDestination = Graph.NAVIGATION_BAR_SCREEN_GRAPH,
    ) {
        mainNavGraph(rootNavController = rootNavController, innerPadding = innerPadding,navController=navController)
//        composable(
//            route = "label_detail_screen/{label}",
//            arguments = listOf(navArgument("label") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val labelJson = backStackEntry.arguments?.getString("label") ?: ""
//            val label = Json.decodeFromString<Label>(labelJson)
//            val labelViewModel: LabelViewmodel = viewModel()
//
//            LabelDetailScreen(
//                rootNavController = rootNavController,
//                label = label,
//                labelViewModel,
//            )
//        }
//
//        // For new notes
//        composable("notes/new") {
//            val noteViewmodel: NotesViewModel = viewModel()
//            val labelViewModel: LabelViewmodel = viewModel()
//            NotesDetailScreenNew(
//                rootNavController = rootNavController,
//                getedNoteId = "",
//                isNewNote = true,
//                noteViewmodel,
//                labelViewModel
//            )
//        }
//
//// For editing notes
//
//        composable(
//            route = "notes/edit/{note_id}",
//            arguments = listOf(navArgument("note_id") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val noteId = backStackEntry.arguments?.getString("note_id") ?: ""
//            val noteViewmodel: NotesViewModel = viewModel()
//            val labelViewModel: LabelViewmodel = viewModel()
//
//            NotesDetailScreenNew(
//                rootNavController = rootNavController,
//                getedNoteId = noteId, // This can be null initially
//                isNewNote = false,
//                notesViewModel = noteViewmodel,
//                labelViewmodel = labelViewModel
//            )
//        }
//        composable(
//            route = "folder_detail_screen/{folder}",
//            arguments = listOf(navArgument("folder") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val noteJson = backStackEntry.arguments?.getString("folder") ?: ""
//            val folder = Json.decodeFromString<Folder>(noteJson)
//            val folderViewModel: FolderViewModel = viewModel()
//            val notesViewModel: NotesViewModel = viewModel()
//
//            FolderDetailScreen(
//                rootNavController = rootNavController,
//                folder = folder,
//                folderViewModel = folderViewModel,
//                notesViewModel = notesViewModel,
//            )
//        }
//        composable(
//            route = "moveNotes/{noteJsonArg}",
//            arguments = listOf(navArgument("noteJsonArg") {
//                type = NavType.StringType
//            })
//        ) { backStackEntry ->
//            // Retrieve the JSON string from the navigation argument
//            val selectedNotesJson = backStackEntry.arguments?.getString("noteJsonArg") ?: ""
//            val folder = Json.decodeFromString<List<String>>(selectedNotesJson)
//            val folderViewModel: FolderViewModel = viewModel()
//
//            MoveNotesScreen(
//                selectedNotes = folder,
//                moveNotesViewModel = folderViewModel,
//                rootNavController = rootNavController
//            )
//        }

    }
}
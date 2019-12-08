package com.dustedduke.findrecipe

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File


class RecipeRepository {

    companion object {
        private const val RECIPES_COLLECTION = "recipes"
        private const val RECIPE_IMAGES_FOLDER = "recipeImages"
        private const val FAVORITES_COLLECTION = "favorites"
        private const val RECIPES_FIELD_ID = "id"
        private const val RECIPES_FIELD_TITLE = "title"
        private const val RECIPES_FIELD_DESCRIPTION = "description"
        private const val RECIPES_FIELD_STEPS = "steps"
        private const val RECIPES_FIELD_INGREDIENTS = "ingredients"
        private const val RECIPES_FIELD_CATEGORIES = "categories"
        private const val RECIPES_FIELD_RATING = "rating"
        private const val RECIPES_FIELD_DATE = "date"
        private const val QUERY_LIMIT: Long = 100
    }

    fun getRandomString(length: Int) : String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private val remoteDB = FirebaseFirestore.getInstance()

    private val remoteStorage = FirebaseStorage.getInstance()

    fun uploadRecipeImage(recipeImage: File) {
        val storageRef = remoteStorage.reference
        var file = Uri.fromFile(recipeImage)
        var uploadTask = storageRef.child(RECIPE_IMAGES_FOLDER + "/${file.lastPathSegment}")
            .putFile(file)

        uploadTask.addOnFailureListener {
            Log.d("RECIPE REPOSITORY: ", "Image upload failed: " + it.message)
        }.addOnSuccessListener {
            Log.d("RECIPE REPOSITORY: ", "Image upload successful")
        }

    }

    fun uploadRecipeImage(itemId: String, recipeImagePath: String): UploadTask {

        // TODO upload приходит пустое изображение 0кб

        Log.d("RECIPE REPOSITORY: ", "recipeImagePath: " + recipeImagePath)

        Log.d("RECIPE REPOSITORY", "UPLOAD IMAGE SIZE BEFORE URI: " + File(recipeImagePath).length())

        val storageRef = remoteStorage.reference
        val file = Uri.fromFile(File(recipeImagePath))

        Log.d("RECIPE REPOSITORY", "UPLOAD IMAGE SIZE: " + File(file.path).length())

//        var childRef = storageRef.child(RECIPE_IMAGES_FOLDER + "/${file.lastPathSegment}")
        var childRef = storageRef.child(RECIPE_IMAGES_FOLDER + "/" + itemId + ".jpg")
        var uploadTask = childRef.putFile(file)

        return uploadTask
    }

    private val changeObservable =
        BehaviorSubject.create<List<DocumentSnapshot>> { emitter: ObservableEmitter<List<DocumentSnapshot>> ->
            val listeningRegistration = remoteDB.collection(RECIPES_COLLECTION)
                .addSnapshotListener { value, error ->
                    if (value == null || error != null) {
                        return@addSnapshotListener
                    }

                    if (!emitter.isDisposed) {
                        emitter.onNext(value.documents)
                    }

                    value.documentChanges.forEach {
                        Log.d(
                            "FirestoreTaskRepository",
                            "Data changed type ${it.type} document ${it.document.id}"
                        )
                    }
                }

            emitter.setCancellable { listeningRegistration.remove() }
        }


    fun getRecipeById(recipeId: String): MutableLiveData<Recipe> {

        // TODO попробовать document snapshot
        // TODO попробовать getDocumentChanges

        val fetchedRecipe = MutableLiveData<Recipe>()
        remoteDB.collection(RECIPES_COLLECTION)
            .document(recipeId) //"DXdt5iYu7go0ClcQx1de"
            .get()
            .addOnSuccessListener { documentSnapshot ->
                //fetchedRecipes.value = documentSnapshot.toObjects(Recipe::class.java)
                fetchedRecipe.postValue(documentSnapshot.toObject(Recipe::class.java))
                Log.d("DB TEST", fetchedRecipe.value.toString())
            }

        Log.d("DB TEST RETURN", fetchedRecipe.value.toString())
        return fetchedRecipe
    }

    fun createRecipe(recipe: Recipe) {
        // Both creates and updates a recipe
        if(recipe.id.isEmpty()) {
            recipe.id = getRandomString(20)
        }
        val recipeToWrite = hashMapOf(
            "id" to recipe.id,
            "title" to recipe.title,
            "author" to recipe.author,
            "date" to recipe.date,
            "image" to recipe.image,
            "description" to recipe.description,
            "categories" to recipe.categories,
            "ingredients" to recipe.ingredients,
            "rating" to recipe.rating,
            "steps" to recipe.steps
        )

        remoteDB.collection(RECIPES_COLLECTION).document(recipe.id)
            .set(recipeToWrite)
            .addOnSuccessListener {
                Log.d("RECIPEREPOSITORY: ", "Recipe write success")
            }
            .addOnFailureListener {
                Log.d("RECIPEREPOSITORY: ", "Recipe write failure: " + it.message)
            }

    }


//    fun getRecipesInOrder(orderType: String = RECIPES_FIELD_RATING, number: Long = QUERY_LIMIT): MutableLiveData<List<Recipe>> {
//
//        val fetchedRecipes = MutableLiveData<List<Recipe>>()
//        remoteDB.collection(RECIPES_COLLECTION)
//            .orderBy(orderType)
//            .limit(number)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                fetchedRecipes.value = (querySnapshot.toObjects(Recipe::class.java))
//                Log.d("DB TEST", fetchedRecipes.value.toString())
//            }
//            .addOnFailureListener {
//                Log.d("ERROR", "Error getting ${number} popular recipes")
//            }
//
//
//        Log.d("DB TEST RETURN", fetchedRecipes.value.toString())
//        return fetchedRecipes
//    }

    fun getRecipesInOrder(
        orderType: String = RECIPES_FIELD_RATING,
        number: Long = QUERY_LIMIT
    ): MutableLiveData<List<Recipe>> {

        // TODO попробовать document snapshot
        // TODO попробовать getDocumentChanges

        val fetchedRecipes = MutableLiveData<List<Recipe>>()
        remoteDB.collection(RECIPES_COLLECTION)
            .orderBy(orderType)
            .limit(number)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                //fetchedRecipes.value = documentSnapshot.toObjects(Recipe::class.java)
                fetchedRecipes.postValue(documentSnapshot.toObjects(Recipe::class.java))
                Log.d("DB TEST", fetchedRecipes.value.toString())
            }

        Log.d("DB TEST RETURN", fetchedRecipes.value.toString())
        return fetchedRecipes
    }


    fun getRecipesInOrderWithEqual(
        filterType: String,
        filterValue: String,
        orderType: String = RECIPES_FIELD_RATING,
        number: Long = QUERY_LIMIT
    ): List<Recipe> {
        var recipesOfCategory = emptyList<Recipe>()
        remoteDB.collection(RECIPES_COLLECTION)
            .whereEqualTo(filterType, filterValue)
            .orderBy(orderType)
            .limit(number)
            .get()
            .addOnSuccessListener { querySnapshot ->
                recipesOfCategory = querySnapshot.toObjects(Recipe::class.java)
            }
            .addOnFailureListener {
                Log.d("ERROR", "Error getting recipes with ${filterType} equals ${filterValue}")
            }

        return recipesOfCategory
    }

    fun getRecipesInOrderWithGreaterOfEqual(
        filterType: String,
        filterValue: String,
        orderType: String = RECIPES_FIELD_RATING,
        number: Long = QUERY_LIMIT
    ): List<Recipe> {
        var recipesOfCategory = emptyList<Recipe>()
        remoteDB.collection(RECIPES_COLLECTION)
            .whereGreaterThanOrEqualTo(filterType, filterValue)
            .orderBy(orderType)
            .limit(number)
            .get()
            .addOnSuccessListener { querySnapshot ->
                recipesOfCategory = querySnapshot.toObjects(Recipe::class.java)
            }
            .addOnFailureListener {
                Log.d("ERROR", "Error getting recipes with ${filterType} equals ${filterValue}")
            }

        return recipesOfCategory
    }


    // Specializations

    fun getRecipesWithCategory(category: String): List<Recipe> {
        return getRecipesInOrderWithEqual("category", category)
    }

    fun getRecipesWithIngredients(
        ingredients: List<String>,
        orderType: String = RECIPES_FIELD_RATING,
        number: Long = QUERY_LIMIT
    ): List<Recipe> {
        var recipesWithIngredients = emptyList<Recipe>()

        // Partial filtering, since not supported by Firestore API
        remoteDB.collection(RECIPES_COLLECTION)
            .whereArrayContains("ingredients", ingredients.get(0))
            .orderBy(orderType)
            .limit(number)
            .get()
            .addOnSuccessListener { querySnapshot ->
                recipesWithIngredients = querySnapshot.toObjects(Recipe::class.java)
            }
            .addOnFailureListener {
                Log.d("ERROR", "Error getting recipes with ingredients: ${ingredients}")
            }

        // Filtering on device
        return recipesWithIngredients.filter { it.categories.containsAll(ingredients) }
    }


    private fun mapDocumentToRemoteTask(document: DocumentSnapshot) =
        document.toObject(Recipe::class.java)!!.apply { id = document.id }

    fun getChangeObservable(): Observable<List<Recipe>> =
        changeObservable.hide()
            .observeOn(Schedulers.io())
            .map { list -> list.map(::mapDocumentToRemoteTask) }


    fun updateFavoriteRecipes(recipeId: String) {
        val mFirebaseAuth = FirebaseAuth.getInstance()
        val mFirebaseUser = mFirebaseAuth.currentUser
        val favoritesRef = remoteDB.collection(FAVORITES_COLLECTION)//.document(mFirebaseUser!!.uid)


        favoritesRef.whereArrayContains("recipes", recipeId).get()
            .addOnSuccessListener { querySnapshot ->
                if(querySnapshot.isEmpty) {
                    Log.d("RECIPEREPOSITORY", "Adding recipe " + recipeId + " to " + mFirebaseUser!!.uid)

                    favoritesRef.document(mFirebaseUser!!.uid)
                        .update(RECIPES_COLLECTION, FieldValue.arrayUnion(recipeId)).addOnFailureListener{
                            Log.d("RECIPEREPOSITORY", "ADDITION FAILED: " + it.message)
                        }

                } else {

                    Log.d("RECIPEREPOSITORY", "Deleting recipe " + recipeId + " from " + mFirebaseUser!!.uid)

                    favoritesRef.document(mFirebaseUser!!.uid)
                        .update(RECIPES_COLLECTION, FieldValue.arrayRemove(recipeId)).addOnFailureListener{
                            Log.d("RECIPEREPOSITORY", "DELETION FAILED: " + it.message)
                        }

                }

            }

        // TODO возвращение значения для изменения кнопки

    }






//    fun getRecipeById(recipeId: String): Recipe {
//        var recipe = Recipe()
//        remoteDB.collection(RECIPES_COLLECTION)
//            .document(recipeId)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                recipe = querySnapshot.toObject(Recipe::class.java)!! // ? !!
//            }
//            .addOnFailureListener { exception ->
//                Log.d("ERROR", "Error getting ${recipeId}")
//            }
//
//        return recipe
//    }
//
//
//    fun getRecipesInOrder(orderType: String = RECIPES_FIELD_RATING, number: Long = QUERY_LIMIT): List<Recipe> {
//        var popularRecipes = emptyList<Recipe>()
//        remoteDB.collection(RECIPES_COLLECTION)
//            .orderBy(orderType)
//            .limit(number)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                popularRecipes = querySnapshot.toObjects(Recipe::class.java)
//                Log.d("DB TEST", popularRecipes.toString())
//            }
//            .addOnFailureListener {
//                Log.d("ERROR", "Error getting ${number} popular recipes")
//            }
//
//        return popularRecipes
//    }
//
//    fun getRecipesInOrderWithEqual(filterType: String,
//                                   filterValue: String,
//                                   orderType: String = RECIPES_FIELD_RATING,
//                                   number: Long = QUERY_LIMIT): List<Recipe> {
//        var recipesOfCategory = emptyList<Recipe>()
//        remoteDB.collection(RECIPES_COLLECTION)
//            .whereEqualTo(filterType, filterValue)
//            .orderBy(orderType)
//            .limit(number)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                recipesOfCategory = querySnapshot.toObjects(Recipe::class.java)
//            }
//            .addOnFailureListener {
//                Log.d("ERROR", "Error getting recipes with ${filterType} equals ${filterValue}")
//            }
//
//        return recipesOfCategory
//    }
//
//    fun getRecipesInOrderWithGreaterOfEqual(filterType: String,
//                                   filterValue: String,
//                                   orderType: String = RECIPES_FIELD_RATING,
//                                   number: Long = QUERY_LIMIT): List<Recipe> {
//        var recipesOfCategory = emptyList<Recipe>()
//        remoteDB.collection(RECIPES_COLLECTION)
//            .whereGreaterThanOrEqualTo(filterType, filterValue)
//            .orderBy(orderType)
//            .limit(number)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                recipesOfCategory = querySnapshot.toObjects(Recipe::class.java)
//            }
//            .addOnFailureListener {
//                Log.d("ERROR", "Error getting recipes with ${filterType} equals ${filterValue}")
//            }
//
//        return recipesOfCategory
//    }
//
//
//    // Specializations
//
//    fun getRecipesWithCategory(category: String): List<Recipe> {
//        return getRecipesInOrderWithEqual("category", category)
//    }
//
//    fun getRecipesWithIngredients(ingredients: List<String>,
//                                  orderType: String = RECIPES_FIELD_RATING,
//                                  number: Long = QUERY_LIMIT): List<Recipe> {
//        var recipesWithIngredients = emptyList<Recipe>()
//
//        // Partial filtering, since not supported by Firestore API
//        remoteDB.collection(RECIPES_COLLECTION)
//            .whereArrayContains("ingredients", ingredients.get(0))
//            .orderBy(orderType)
//            .limit(number)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                recipesWithIngredients = querySnapshot.toObjects(Recipe::class.java)
//            }
//            .addOnFailureListener {
//                Log.d("ERROR", "Error getting recipes with ingredients: ${ingredients}")
//            }
//
//        // Filtering on device
//        return recipesWithIngredients.filter { it.categories.containsAll(ingredients)}
//    }



}
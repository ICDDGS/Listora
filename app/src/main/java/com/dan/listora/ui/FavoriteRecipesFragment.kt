package com.dan.listora.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dan.listora.application.ListDBApp
import com.dan.listora.data.db.model.RecipeEntity
import com.dan.listora.databinding.FragmentRecipesBinding
import com.dan.listora.ui.RecipeDetailActivity
import com.dan.listora.ui.adapter.RecipeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteRecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecipeAdapter
    private var favoriteRecipes: MutableList<RecipeEntity> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRecetas.layoutManager = LinearLayoutManager(requireContext())

        adapter = RecipeAdapter(favoriteRecipes,
            onItemClick = { recipe ->
                val intent = Intent(requireContext(), RecipeDetailActivity::class.java)
                intent.putExtra("recipe_id", recipe.id)
                startActivity(intent)
            },
            onToggleFavorite = { recipe ->
                lifecycleScope.launch {
                    val dao = (requireActivity().application as ListDBApp).database.recipeDAO()
                    val updated = recipe.copy(isFavorite = !recipe.isFavorite)
                    withContext(Dispatchers.IO) {
                        dao.updateRecipe(updated)
                    }
                    loadFavorites()
                }
            }
        )

        binding.rvRecetas.adapter = adapter
        loadFavorites()
    }

    private fun loadFavorites() {
        lifecycleScope.launch {
            val dao = (requireActivity().application as ListDBApp).database.recipeDAO()
            val result = withContext(Dispatchers.IO) {
                dao.getFavoriteRecipes()
            }
            favoriteRecipes.clear()
            favoriteRecipes.addAll(result)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
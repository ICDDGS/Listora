package com.dan.listora.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dan.listora.ui.FavoriteRecipesFragment
import com.dan.listora.ui.fragments.RecipesFragment

class RecipePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RecipesFragment()
            1 -> FavoriteRecipesFragment()
            else -> RecipesFragment()
        }
    }
}
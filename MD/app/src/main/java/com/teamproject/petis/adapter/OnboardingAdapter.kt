package com.teamproject.petis.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamproject.petis.onboarding.FirstPageFragment
import com.teamproject.petis.onboarding.SecondPageFragment
import com.teamproject.petis.onboarding.ThirdPageFragment

class OnboardingAdapter(
    activity: FragmentActivity,
    private val onLastPageCallback: ((Int) -> Unit)? = null
) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FirstPageFragment()
            1 -> SecondPageFragment()
            2 -> ThirdPageFragment {
                onLastPageCallback?.invoke(position)
            }
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
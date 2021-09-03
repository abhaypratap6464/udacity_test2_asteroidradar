package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidFilter
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val viewModel: MainViewModel by lazy {
        val viewModelFactory =
            MainViewModelFactory(AsteroidRepository(getDatabase(requireContext().applicationContext)))
        ViewModelProvider(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initializeView(inflater)

        subscribeObservable()

        return binding.root
    }

    /**
     * subscribe all the observable from MainViewModel
     */
    private fun subscribeObservable() {
        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner, {
            if (it != null) {
                navigateToDetailFragment(it)
            }
        })
    }

    /**
     * navigate to Detail fragment to show the details of selected asteroid
     * @param selectedAsteroid
     */
    private fun navigateToDetailFragment(selectedAsteroid: Asteroid) {
        findNavController().navigate(
            MainFragmentDirections.actionShowDetail(
                selectedAsteroid
            )
        )
    }

    /**
     *  initializes all Views in fragment
     */
    private fun initializeView(inflater: LayoutInflater) {

        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter = AsteroidAdapter(
            AsteroidAdapter.OnClickListener {
                viewModel.displayAsteroidDetails(it)
            }
        )

        setHasOptionsMenu(true)
    }


    /**
     * for menu
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
            when (item.itemId) {
                R.id.show_today_asteroids -> AsteroidFilter.CURRENT_DAY
                R.id.show_week_asteroids -> AsteroidFilter.CURRENT_WEEK
                R.id.show_saved_asteroids -> AsteroidFilter.SAVED
                else -> AsteroidFilter.SAVED
            }
        )
        return true
    }


}

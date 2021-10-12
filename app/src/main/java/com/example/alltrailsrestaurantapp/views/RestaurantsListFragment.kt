package com.example.restaurantapp

import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alltrailsrestaurantapp.R
import com.example.alltrailsrestaurantapp.databinding.RestaurantsListBinding
import com.example.alltrailsrestaurantapp.models.Result
import com.example.alltrailsrestaurantapp.views.RestaurantsListAdapter
import com.example.alltrailsrestaurantapp.views.viewmodels.RestaurantViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestaurantsListFragment : Fragment() {

    private var _binding: RestaurantsListBinding? = null
    private val binding get() = _binding!!

    private val restaurantViewModel by activityViewModels<RestaurantViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RestaurantsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: RestaurantsListFragmentArgs by navArgs()

        _binding?.listRecycleView?.layoutManager = LinearLayoutManager(this.activity)
        var adapter = RestaurantsListAdapter(
            activity?.baseContext,
            mutableListOf(),
            ContextCompat.getDrawable(activity!!, R.drawable.baseline_favorite_24),
            ContextCompat.getDrawable(activity!!, R.drawable.baseline_favorite_border_24),
            this::updateFavoriteRestaurant
        )
        _binding?.listRecycleView?.adapter = adapter

        lifecycleScope.launch {
            restaurantViewModel.uiState.collect {
                adapter.dataSet = it
                adapter.notifyDataSetChanged()
            }
        }

        lifecycleScope.launch {
            restaurantViewModel.errorMessage.observeForever {
                Toast.makeText(activity,it.buildMessage(context), Toast.LENGTH_SHORT).show()
            }
        }

        _binding?.sortBtn?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lifecycleScope.launch {
                    restaurantViewModel.sortChannel.send(true)
                }
            } else {
                lifecycleScope.launch {
                    restaurantViewModel.sortChannel.send(false)
                }
            }
        }

        _binding?.searchView?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                searchTxt: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                lifecycleScope.launch {
                    restaurantViewModel.searchChannel.send(searchTxt.toString())
                }
            }
        })

        _binding?.refreshContainer?.setOnRefreshListener {
            lifecycleScope.launch {
                var location: Location = Location("")
                location.longitude = args.longitude.toDouble()
                location.latitude = args.latitude.toDouble()
                restaurantViewModel.currentLocationChannel.send(location)
            }
            if (_binding?.refreshContainer?.isRefreshing() == true) {
                _binding?.refreshContainer?.setRefreshing(false);
            }
        }

        binding.btnMap.setOnClickListener {
            val action = RestaurantsListFragmentDirections.actionSecondFragmentToFirstFragment()
            findNavController().navigate(action)
        }
    }

    fun updateFavoriteRestaurant(result: Result, isFavorite: Boolean) {
        if (isFavorite) {
            lifecycleScope.launch {
                restaurantViewModel.addFavoriteRestaurantChannel.send(result)
            }
        }
        if (!isFavorite) {
            lifecycleScope.launch {
                restaurantViewModel.removeFavoriteRestaurantChannel.send(result)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
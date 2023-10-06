package ru.netology.multimedia.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.multimedia.MediaLifecycleObserver
import ru.netology.multimedia.adapter.MediaAdapter
import ru.netology.multimedia.adapter.OnPlayListener
import ru.netology.multimedia.databinding.FragmentMainBinding
import ru.netology.multimedia.dto.Track
import ru.netology.multimedia.viewmodel.MediaViewModel

class MainFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()

        const val BASE_URL =
            "https://github.com/netology-code/andad-homeworks/raw/master/09_multimedia/data/"
        val observer = MediaLifecycleObserver()
    }
    private var currentPlayTrackId = -1L
    private var nextPosition = 0
    lateinit var binding: FragmentMainBinding
    private lateinit var adapter: MediaAdapter
    private val model: MediaViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        lifecycle.addObserver(observer)

        binding.mediaList.layoutManager = LinearLayoutManager(requireContext())
        adapter = MediaAdapter(object : OnPlayListener {

            @SuppressLint("NotifyDataSetChanged")
            override fun onPlay(track: Track, seekBar: SeekBar, position: Int) {
                if (observer.isPaused() == true && track.id == currentPlayTrackId) {
                    observer.mediaPlayer?.start()
                } else {
                    nextPosition = position
                    currentPlayTrackId = track.id
                    model.setStateTracks(track.id)
                    model.playMediaplayer(track, seekBar)
                    binding.mediaList.adapter?.notifyDataSetChanged()
                }

                observer.mediaPlayer?.setOnCompletionListener {
                    nextPosition = if (nextPosition < adapter.itemCount - 1) (nextPosition + 1) else 0
                    val nextTrack = adapter.currentList[nextPosition]
                    currentPlayTrackId = nextTrack.id
                    model.setStateTracks(nextTrack.id)
                    binding.mediaList.scrollToPosition(nextPosition)
                    val nextSeek =  binding.mediaList
                        .findViewHolderForAdapterPosition(nextPosition)?.itemView?.findViewById<SeekBar>(seekBar.id)
                    nextSeek?.let {
                            it1 -> model.playMediaplayer(nextTrack, it1)
                    }
                    binding.mediaList.adapter?.notifyDataSetChanged()

                }
            }

            override fun onPause() {
                model.onPause()
            }

        })

        binding.mediaList.adapter = adapter

        binding.getMedia.setOnClickListener {
            binding.getMedia.isVisible = false
            try {
                model.loadMedia()
                binding.progress.isVisible = true
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Media list not found", Toast.LENGTH_LONG)
                    .show()
                binding.getMedia.isVisible = true
                binding.progress.isVisible = false
            }
        }

        model.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        model.media.observe(viewLifecycleOwner) {
            with(binding) {
                titleView.text = it.title
                authorView.text = it.artist
                cardView.isVisible = true
                progress.isVisible = false
            }
        }

        return binding.root
    }

}
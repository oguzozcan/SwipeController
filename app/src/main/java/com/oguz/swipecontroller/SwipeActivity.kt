package com.oguz.swipecontroller

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oguz.swipelibrary.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

private const val AGE_LIMIT = 29
private const val TAG = "SwipeV2Act"
private const val DATA_FILE_PATH = "players.csv"

class SwipeActivity : AppCompatActivity(),
    OnSwipeStateChangedListener<PlayersDataAdapter.SampleViewHolder?>,
    SwipeCallback.OnResetSwipeController,
    OnSwipeActionLaunchedListener<PlayersDataAdapter.SampleViewHolder?> {

    private lateinit var callback: SwipeCallback<PlayersDataAdapter.SampleViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)
        setupRecyclerView()
        findViewById<View>(R.id.reset).setOnClickListener { setupRecyclerView() }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val adapter = createAdapter() as RecyclerView.Adapter<PlayersDataAdapter.SampleViewHolder>
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        callback = SwipeCallback.Builder<PlayersDataAdapter.SampleViewHolder>()
            .setRecyclerView(recyclerView)
            .setAdapter(adapter)
            .setOnSwipeActionLaunchedListener(this)
            .setOnSwipeStateChangedListener(this)
            .build()
    }

    private fun createAdapter(): PlayersDataAdapter {
        val players: MutableList<Player> = ArrayList()
        InputStreamReader(resources.assets.open(DATA_FILE_PATH)).use { inputStream ->
            val reader = BufferedReader(inputStream)
            var line = reader.readLine()
            var data: Array<String>
            while (reader.readLine().also { line = it } != null) {
                data = line.split(",").toTypedArray()
                val player = Player()
                player.name = data[0]
                player.nationality = data[1]
                player.club = data[4]
                player.rating = data[9].toInt()
                player.age = data[14].toInt()
                players.add(player)
                if (player.age > AGE_LIMIT) {
                    player.viewHolderType = BaseSwipeViewHolder.ViewHolderType.THREE_ACTION_BUTTON
                } else {
                    player.viewHolderType = BaseSwipeViewHolder.ViewHolderType.TWO_ACTION_BUTTON
                }
            }
        }
        return PlayersDataAdapter(this, players, this)
    }

    override fun onSwipeStarted(nextHolder: PlayersDataAdapter.SampleViewHolder) {
        Log.d(TAG, "ON SWIPE STARTED " + nextHolder.layoutPosition)
    }

    override fun onSwipeEnded(prevHolder: PlayersDataAdapter.SampleViewHolder) {
        Log.d(TAG, "ON SWIPE ENDED " + prevHolder.layoutPosition)
    }

    override fun onSwipeActionLaunched(viewHolder: PlayersDataAdapter.SampleViewHolder, swipeAction: SwipeActionProvider.SwipeAction) {
        val actionName = getString(swipeAction.text)
        Toast.makeText(
            this,
            "TRIGGER BUTTON ACTION: " + actionName + " - adapterPos: " + viewHolder.layoutPosition,
            Toast.LENGTH_SHORT
        ).show()
        resetSwipeController()
    }

    override fun onItemClicked(viewHolder: PlayersDataAdapter.SampleViewHolder) {
        Toast.makeText(
            this,
            "TRIGGER ITEM CLICK -adapterPos : " + viewHolder.layoutPosition,
            Toast.LENGTH_SHORT
        ).show()
        Log.d(TAG, "TRIGGER ITEM CLICK -adapterPos : " + viewHolder.layoutPosition)
        resetSwipeController()
    }

    override fun onItemLongClicked(viewHolder: PlayersDataAdapter.SampleViewHolder) {
        Toast.makeText(
            this,
            "TRIGGER ITEM LONG CLICK -adapterPos : " + viewHolder.layoutPosition,
            Toast.LENGTH_SHORT
        ).show()
        resetSwipeController()
    }

    override fun resetSwipeController() {
        callback.resetSwipeController()
    }
}


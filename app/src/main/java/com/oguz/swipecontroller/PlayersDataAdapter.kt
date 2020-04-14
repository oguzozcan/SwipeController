package com.oguz.swipecontroller

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oguz.swipelibrary.BaseSwipeViewHolder
import com.oguz.swipelibrary.SwipeActionProvider
import com.oguz.swipelibrary.SwipeCallback

class PlayersDataAdapter(private val context: Context, private val players: List<Player>, private val resetSwipeControllerListener: SwipeCallback.OnResetSwipeController) : RecyclerView.Adapter<BaseSwipeViewHolder>() {

    inner class SampleViewHolder(viewGroup: ViewGroup, foreGroundLayoutId: Int, holderTypeId: Int) : BaseSwipeViewHolder(viewGroup, foreGroundLayoutId, holderTypeId, resetSwipeControllerListener) {
        val name: TextView = itemView.findViewById(R.id.name)
        val nationality: TextView = itemView.findViewById(R.id.nationality)
        val club: TextView = itemView.findViewById(R.id.club)
        val rating: TextView = itemView.findViewById(R.id.rating)
        val age: TextView = itemView.findViewById(R.id.age)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSwipeViewHolder {
        return SampleViewHolder(parent, R.layout.player_row, viewType)
    }

    override fun onBindViewHolder(holder: BaseSwipeViewHolder, position: Int) {
        val player = getItem(position)
        val sampleViewHolder = holder as SampleViewHolder
        sampleViewHolder.name.text = player.name
        sampleViewHolder.nationality.text = player.nationality
        sampleViewHolder.club.text = player.club
        sampleViewHolder.rating.text = player.rating.toString()
        sampleViewHolder.age.text = player.age.toString()
        sampleViewHolder.bind(context, getActions(player))
    }

    private fun getActions(record: Player): Array<SwipeActionProvider.SwipeAction> {
        return when (record.viewHolderType) {
            BaseSwipeViewHolder.ViewHolderType.ONE_ACTION_BUTTON -> arrayOf(ActionType.ONE)
            BaseSwipeViewHolder.ViewHolderType.TWO_ACTION_BUTTON -> arrayOf(ActionType.ONE, ActionType.TWO)
            BaseSwipeViewHolder.ViewHolderType.THREE_ACTION_BUTTON -> arrayOf(ActionType.ONE, ActionType.TWO, ActionType.THREE)
            else -> emptyArray()
        }
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position).viewHolderType?.let {
            return it.getTypeId()
        }
        return BaseSwipeViewHolder.ViewHolderType.NO_ACTION_BUTTON.getTypeId()
    }

    override fun getItemCount(): Int {
        return players.size
    }

    private fun getItem(position: Int): Player {
        return players[position]
    }
}

package fgietzen.simpleruntracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fgietzen.simpleruntracker.persistence.Run
import fgietzen.simpleruntracker.utils.displayTime
import java.util.function.Consumer

class RunEntryAdapter(
    private val dataset: List<Run>,
    private val deleteButtonListener: Consumer<Int?>,
    private val openButtonListener: Consumer<Int?>,
) : RecyclerView.Adapter<RunEntryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textDistance: TextView = view.findViewById(R.id.distance);
        val textTime: TextView = view.findViewById(R.id.time);
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.run_entry, viewGroup, false);
        val holder = ViewHolder(view);

        val openButton: ImageButton = view.findViewById(R.id.open);
        openButton.setOnClickListener {
            openButtonListener.accept(dataset[holder.adapterPosition].id);
        }

        val deleteButton: ImageButton = view.findViewById(R.id.delete);
        deleteButton.setOnClickListener {
            deleteButtonListener.accept(dataset[holder.adapterPosition].id);
            viewGroup.removeView(view);
        }

        return holder;
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textDistance.text = String.format("%.2f km", dataset[position].distance / 1000.0);
        viewHolder.textTime.text = displayTime(dataset[position].time);
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}
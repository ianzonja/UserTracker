package cz.mendelu.pef.locationbasedservices

import android.view.View

interface ClickListener {
    fun onPositionClicked(view: View, position: Int)
}
package com.jacim3.miseforyou.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jacim3.miseforyou.R
import com.jacim3.miseforyou.addons.ForeCastAssembler


class SecondFragment : Fragment() {

    var loadingCheck = false
    var assembler: ForeCastAssembler? = null
    private var onCreateBool = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_second, container, false)

        return view
    }


    companion object {

        fun newInstance(): SecondFragment {
            val fragment = SecondFragment()
            /*val args = Bundle()
            try {
                args.putStringArrayList("tagArray", data?.get(TAG))
                args.putStringArrayList("dataArray", data?.get(DATA))
            } catch (e: IndexOutOfBoundsException) {

            }
            fragment.arguments = args

             */
            return fragment
        }
    }
}
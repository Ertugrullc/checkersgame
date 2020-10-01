package com.example.checkersgame

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.checkersgame.databinding.FragmentGameBinding
import kotlinx.android.synthetic.main.win_toast.*


class GameFragment : Fragment() {

    private lateinit var vModel : GameFragmentViewModel
    private lateinit var binding: FragmentGameBinding
    private lateinit var currentSquare : Square
    private lateinit var winToast: Toast
    private lateinit var textView: TextView
    private lateinit var toastView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        vModel = ViewModelProvider(this).get(GameFragmentViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_game,container,false)

        winToast = Toast(this.context)
        winToast.setGravity(Gravity.CENTER,0,0)
        winToast.duration = Toast.LENGTH_LONG
        toastView = inflater.inflate(R.layout.win_toast,null)
        textView = toastView.findViewById<TextView>(R.id.tView)
        drawBoard()

        onClickListeners()

        binding.button.setOnClickListener{
            vModel.setBoard()
            drawBoard()
        }




        return binding.root
    }



    private fun drawBoard(){
        for (row in 0 until 8){
            for (column in 0 until 8){
                currentSquare = vModel.board[row*8+column]
                val currentView = findImageView(row, column)
                currentView.scaleType = ImageView.ScaleType.CENTER_INSIDE

                if(currentSquare.isSelected)
                    currentView.setBackgroundResource(R.drawable.selected_grid)
                else
                    currentView.setBackgroundResource(android.R.color.transparent)
                if(!currentSquare.piece.isKing)
                when(currentSquare.piece.team){
                    vModel.P_BLACK ->currentView.setImageResource(R.drawable.black)

                    0 -> {
                        if(currentSquare.isTarget)
                            currentView.setImageResource(R.drawable.point)
                        else
                            currentView.setImageResource(android.R.color.transparent)}

                    vModel.P_WHITE-> currentView.setImageResource(R.drawable.white)
                }
                else
                when(currentSquare.piece.team){
                    vModel.P_BLACK ->currentView.setImageResource(R.drawable.black_king)
                    else -> currentView.setImageResource(R.drawable.white_king)
                }



            }
        }
    }

    private fun onClickListeners(){
        for (row in 0 until 8){
            for (column in 0 until 8){
                val currentView = findImageView(row, column)
                currentView.setOnClickListener(){
                    vModel.clickActions(row,column)
                    drawBoard()
                    if (vModel._state==vModel.P_WHITE*vModel.won){
                        textView.text = "PLAYER WHITE WON!!"
                        winToast.view=toastView
                        winToast.show()
                    }
                    else if (vModel._state==vModel.P_BLACK*vModel.won){
                        textView.text ="PLAYER BLACK WON!!"
                        winToast.view=toastView
                        winToast.show()
                    }
                }
            }
        }
    }

    private fun findImageView(row: Int, column: Int): ImageView {
        val currentView = when (row * 8 + column) {
            0 -> binding.A8
            1 -> binding.B8
            2 -> binding.C8
            3 -> binding.D8
            4 -> binding.E8
            5 -> binding.F8
            6 -> binding.G8
            7 -> binding.H8
            8 -> binding.A7
            9 -> binding.B7
            10 -> binding.C7
            11 -> binding.D7
            12 -> binding.E7
            13 -> binding.F7
            14 -> binding.G7
            15 -> binding.H7
            16 -> binding.A6
            17 -> binding.B6
            18 -> binding.C6
            19 -> binding.D6
            20 -> binding.E6
            21 -> binding.F6
            22 -> binding.G6
            23 -> binding.H6
            24 -> binding.A5
            25 -> binding.B5
            26 -> binding.C5
            27 -> binding.D5
            28 -> binding.E5
            29 -> binding.F5
            30 -> binding.G5
            31 -> binding.H5
            32 -> binding.A4
            33 -> binding.B4
            34 -> binding.C4
            35 -> binding.D4
            36 -> binding.E4
            37 -> binding.F4
            38 -> binding.G4
            39 -> binding.H4
            40 -> binding.A3
            41 -> binding.B3
            42 -> binding.C3
            43 -> binding.D3
            44 -> binding.E3
            45 -> binding.F3
            46 -> binding.G3
            47 -> binding.H3
            48 -> binding.A2
            49 -> binding.B2
            50 -> binding.C2
            51 -> binding.D2
            52 -> binding.E2
            53 -> binding.F2
            54 -> binding.G2
            55 -> binding.H2
            56 -> binding.A1
            57 -> binding.B1
            58 -> binding.C1
            59 -> binding.D1
            60 -> binding.E1
            61 -> binding.F1
            62 -> binding.G1
            else -> binding.H1
        }
        return currentView
    }


}
package com.example.checkersgame

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlin.math.abs
import kotlin.math.sign


class GameFragmentViewModel : ViewModel(){
    //states
    private val turnWhite = -1
    private val turnBlack = 1
    private val pieceNotSelected = 1
    private val pieceSelected = 2
    private val mustPlay = 3
    val won = 4
    private var mustEatB = false
    private var mustEatW = false
    private var pMoves : MutableList<Int> = mutableListOf()
    private var selectedSquare = -1
    private var state = turnWhite*pieceNotSelected
    val _state
        get() = state
    //players
    val  P_BLACK = 1
    val  P_WHITE = -1
    val  NO_PIECE = 0
    private var whitePieces = 12
    private var blackPieces = 12
    //////////////////////////////////////////////////////
    private lateinit var _board : MutableList<Square>
    val board : List<Square>
        get()=_board


    init {
        setBoard()
    }


    fun setBoard(){
        blackPieces=12
        whitePieces=12
        selectedSquare=-1
        state = turnWhite*pieceNotSelected
        _board = mutableListOf()
        for (row in 0 until 8)
            for(col in 0 until 8){
                if(row<3 && (row+col)%2==0){
                    _board.add(Square(row*8+col, Piece(P_BLACK,false),false,false))
                }
                else if (row>4 && (row+col)%2==0){
                    _board.add(Square(row*8+col, Piece(P_WHITE,false),false,false))
                }
                else{
                    _board.add(Square(row*8+col,Piece(NO_PIECE,false),false,false))
                }
            }
    }


    fun clickActions(row : Int, column : Int){
        val currentSquare= _board[row*8+column]
        when(abs(state)){
            1 -> notSelectedActions(currentSquare)
            2 -> selectedActions(currentSquare)
            3 -> mustPlayActions(currentSquare)
            else -> setBoard()
        }

    }

    private fun resetTarget() {
        while (pMoves.isNotEmpty()){
            val move = pMoves.removeAt(0)
            _board[move].isTarget=false
        }
    }

    private fun notSelectedActions(currentSquare: Square){
        var doCheck = true
        if(sign(state*1.0)==currentSquare.piece.team*1.0)
            {
                selectedSquare=currentSquare.id
                _board[selectedSquare].isSelected=true
                state=currentSquare.piece.team*pieceSelected//state change

                eatCheck(currentSquare.piece.team)

                val mustEat = when(currentSquare.piece.team){
                    P_BLACK -> mustEatB
                    P_WHITE -> mustEatW
                    else -> false
                }
                if(!moveCheck(currentSquare) && mustEat){
                    currentSquare.isSelected=false
                    state = currentSquare.piece.team*pieceNotSelected
                    Log.v("nsif1","test")
                    resetTarget()
                    doCheck = false
                }
                else if (pMoves.isEmpty()){
                    currentSquare.isSelected = false
                }
                resetTarget()
                if (doCheck)
                    moveCheck(currentSquare)

            }
    }

    private fun eatCheck(team : Int) {
        val pieceList = _board.filter { it.piece.team == team}
        val pieceIterator = pieceList.listIterator()
        var mustEat = false
        for (item in pieceIterator) {
            if (moveCheck(item))
                mustEat = true
            Log.v("eatCheck",item.id.toString())
        }

        when(team){
            P_WHITE -> {
                mustEatW = mustEat
            }
            P_BLACK -> {
                mustEatB = mustEat
            }
            else -> {
                Log.v("state","exception")
            }
        }
        Log.v("eatcheck",pMoves.toString())
        if (pMoves.isEmpty())
            state = (-1) * team * won

        resetTarget()
    }

    private fun selectedActions(currentSquare: Square){
        var doCheck = true
        val mustEat = when(_board[selectedSquare].piece.team){
            P_WHITE -> mustEatW
            P_BLACK -> mustEatB
            else -> false
        }
        if(sign(state*1.0)==currentSquare.piece.team*1.0)
        {
            resetTarget()
            _board[selectedSquare].isSelected=false
            selectedSquare=currentSquare.id
            _board[selectedSquare].isSelected=true

            if(!moveCheck(currentSquare) && mustEat){
                currentSquare.isSelected=false
                state = currentSquare.piece.team*pieceNotSelected
                Log.v("sif1","test")
                resetTarget()
                doCheck=false
            }
            else if(pMoves.isEmpty()){
                currentSquare.isSelected=false
            }
            resetTarget()
            if (doCheck)
                moveCheck(currentSquare)
        }
        else if (currentSquare.isTarget){
            resetTarget()

            val targetSquare = currentSquare.id
            val team = _board[selectedSquare].piece.team


            if(abs(targetSquare-selectedSquare)==18 || abs(targetSquare-selectedSquare)==14){

                currentSquare.piece=_board[selectedSquare].piece
                _board[selectedSquare].piece= Piece(NO_PIECE,false)
                kingCheck(currentSquare)
                _board[selectedSquare].isSelected=false

                val eatenPiece = selectedSquare+(targetSquare-selectedSquare)/2
                when(_board[eatenPiece].piece.team){
                    P_WHITE -> whitePieces--
                    else -> blackPieces--
                }

                _board[eatenPiece].piece = Piece(NO_PIECE,false)
                resetTarget()

                if(whitePieces==0){
                    state=P_BLACK*won
                    return
                }
                else if (blackPieces==0){
                    state=P_WHITE*won
                    return
                }

                selectedSquare=targetSquare

                if(moveCheck(_board[selectedSquare])){
                    state=currentSquare.piece.team*mustPlay//state change
                }
                else {
                    selectedSquare=-1
                    state=(-1)*currentSquare.piece.team*pieceNotSelected//state change
                    resetTarget()
                    eatCheck(currentSquare.piece.team*(-1))
                }
            }
            else if(!mustEat){
                currentSquare.piece=_board[selectedSquare].piece
                _board[selectedSquare].piece=Piece(NO_PIECE,false)
                _board[selectedSquare].isSelected=false
                kingCheck(currentSquare)
                state= currentSquare.piece.team * pieceNotSelected * (-1)//state change

                selectedSquare=-1
                resetTarget()
                eatCheck(currentSquare.piece.team)
            }
        }
        else {
            resetTarget()
            _board[selectedSquare].isSelected=false
        }
    }

    private fun mustPlayActions(currentSquare: Square){
        val targetSquare = currentSquare.id
        if((abs(targetSquare-selectedSquare)==18 || abs(targetSquare-selectedSquare)==14)&&currentSquare.isTarget){
            currentSquare.piece=_board[selectedSquare].piece
            _board[selectedSquare].piece= Piece(NO_PIECE,false)
            kingCheck(currentSquare)
            _board[selectedSquare].isSelected=false

            val eatenPiece = selectedSquare+(targetSquare-selectedSquare)/2
            when(_board[eatenPiece].piece.team){
                P_WHITE -> whitePieces--
                else -> blackPieces--
            }

            _board[eatenPiece].piece= Piece(NO_PIECE,false)
            resetTarget()

            if(whitePieces==0){
                state=P_BLACK*won
                return
            }
            else if (blackPieces==0){
                state=P_WHITE*won
                return
            }


            selectedSquare=targetSquare

            if(moveCheck(_board[selectedSquare])){
                state=currentSquare.piece.team*mustPlay//state change
            }
            else {
                selectedSquare=-1
                state=(-1)*currentSquare.piece.team*pieceNotSelected//state change
                resetTarget()
                eatCheck(currentSquare.piece.team*(-1))
            }
        }
    }

    private fun kingCheck(currentSquare: Square){
        when(currentSquare.piece.team){
            P_WHITE -> if (currentSquare.id<8)
                currentSquare.piece.isKing=true
            P_BLACK -> if (currentSquare.id>55)
                currentSquare.piece.isKing=true
            else -> Log.v("kingCheck","exception")
        }
    }

    private fun moveCheck(currentSquare: Square):Boolean{
        var ind: Int
        val piece = currentSquare.piece

        if (currentSquare.id>6 && (piece.team==P_WHITE || piece.isKing)){
            ind=currentSquare.id-7
            if(_board[ind].piece.team==NO_PIECE && ind%8!=0){
                _board[ind].isTarget=true
                pMoves.add(ind)
            }
            if(currentSquare.id>9){
                ind = currentSquare.id-9
                if(_board[ind].piece.team==NO_PIECE && ind%8!=7){
                    _board[ind].isTarget=true
                    pMoves.add(ind)
                }
            }
        }
        if(currentSquare.id<57 && (piece.team==P_BLACK || piece.isKing)){
            ind =currentSquare.id+7
            if (_board[ind].piece.team==NO_PIECE && ind%8!=7){
                _board[ind].isTarget=true
                pMoves.add(ind)
            }
            if(currentSquare.id<55){
                ind = currentSquare.id+9
                if (_board[ind].piece.team==NO_PIECE && ind%8!=0){
                    _board[ind].isTarget=true
                    pMoves.add(ind)
                }
            }
        }
        if(currentSquare.id>13 && (piece.team==P_WHITE || piece.isKing)){
            ind = currentSquare.id-14
            if(_board[ind+7].piece.team==-piece.team && _board[ind].piece.team==NO_PIECE && currentSquare.id%8<6){
                _board[ind].isTarget=true
                while (pMoves.isNotEmpty()){
                    val x = pMoves.removeAt(0)
                    _board[x].isTarget=false
                }
                pMoves.add(ind)
                return true
            }
            if (currentSquare.id>17){
                ind = currentSquare.id-18
                if(_board[ind+9].piece.team==-piece.team && _board[ind].piece.team==NO_PIECE && currentSquare.id%8>1){
                    _board[ind].isTarget=true
                    while (pMoves.isNotEmpty()){
                        val x = pMoves.removeAt(0)
                        _board[x].isTarget=false
                    }
                    pMoves.add(ind)
                    return true
                }
            }
        }
        if (currentSquare.id<50 && (piece.team==P_BLACK || piece.isKing)){
            ind = currentSquare.id+14
            if (_board[ind-7].piece.team==-piece.team && _board[ind].piece.team==NO_PIECE && currentSquare.id%8>1){
                _board[ind].isTarget=true
                while (pMoves.isNotEmpty()){
                    val x = pMoves.removeAt(0)
                    _board[x].isTarget=false
                }
                pMoves.add(ind)
                return true
            }
            if (currentSquare.id<46){
                ind = currentSquare.id+18
                if(_board[ind-9].piece.team==-piece.team && _board[ind].piece.team==NO_PIECE && currentSquare.id%8<6){
                    _board[ind].isTarget=true
                    while (pMoves.isNotEmpty()){
                        val x = pMoves.removeAt(0)
                        _board[x].isTarget=false
                    }
                    pMoves.add(ind)
                    return true
                }
            }
        }

        return false
    }

}
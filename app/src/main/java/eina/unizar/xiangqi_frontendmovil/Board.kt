package eina.unizar.xiangqi_frontendmovil

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.widget.Button
import android.widget.ImageButton
import android.widget.TableLayout
import eina.unizar.xiangqi_frontendmovil.GameLogic.Coordinates
import eina.unizar.xiangqi_frontendmovil.GameLogic.PieceBoard



class Board : AppCompatActivity() {

    enum class State{
        INIT, SHOWING_MOVES, WAITING_OPONENT
    }
    //pieza fila,columna = casillas[columna + from.fila * 10]
    var casillas = arrayOfNulls<ImageButton>(90)
    var tablero = PieceBoard()
    var estado = State.INIT
    var lastClick : Coordinates? = null
    var moves : List<Coordinates> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        //anyadimos de fondo el tablero correspondiente

        val fondo = findViewById<TableLayout>(R.id.board)
        fondo.setBackgroundResource(resources.getIdentifier("skin_board_10", "drawable", packageName))
        //guardamos las casillas como botones
        for (i in 0..9)
            for( j in 0..8){
                casillas[j+(i*9)] = findViewById(resources.getIdentifier("cell$j$i","id", packageName))
                //anyadimos el comportamiento a estos
                val onClickListener = casillas[j + (i * 9)]?.setOnClickListener {
                    clickOnIntersection(Coordinates(i, j))
                    println("$i-$j")
                }
            }

        //mostramos las piezas
        drawBoard()
    }

    fun clickOnIntersection( click : Coordinates ) {
        when(estado){
            State.INIT -> {
                if(showMoves(click)){
                    estado = State.SHOWING_MOVES
                    lastClick = click
                }
            }
            State.SHOWING_MOVES -> {
                hideMoves()
                if (moves.contains(click)){
                    if(movePiece(lastClick,click)) {
                        estado = State.INIT
                        //disableButtons()
                        drawBoard()

                    }
                }
                else{
                    if(showMoves(click)){
                        estado = State.SHOWING_MOVES
                        lastClick = click
                    }else{
                        estado = State.INIT
                    }
                }
            }

            State.WAITING_OPONENT -> {

            }
        }
    }

    private fun disableButtons() {
        for (c in casillas)
            c!!.isEnabled = false
    }
    private  fun enableButtons(){
        for (c in casillas)
            c!!.isEnabled = true
    }

    private fun drawBoard() {
        for (i in 0..9)
            for( j in 0..8){
                val imgName = tablero.getPiece(Coordinates(i,j)).getImgName(1)
                casillas[j+(i*9)]!!.setBackgroundColor(0x00000000)
                if (imgName != null)
                    casillas[j+(i*9)]!!.setImageResource(
                        resources.getIdentifier(imgName,"drawable", packageName))
                else {
                    casillas[j+(i*9)]!!.setImageResource(0)

                }
            }
    }

    private fun showMoves(from: Coordinates): Boolean {
        moves = tablero.getMoves(from)
        if(moves.isEmpty())return false
        else
            for (move in moves){
                println(move)
                casillas[ move.columna + move.fila * 9]?.setBackgroundColor(0x7F00FF00)
            }

        return true
    }

    private fun hideMoves() {
        for (move in moves)
            casillas[ move.columna + move.fila * 9]?.setBackgroundColor(0x00000000)
    }

    private fun movePiece(from: Coordinates?, to: Coordinates): Boolean {
        if (from == null) return false
        if(tablero.move(from,to)){
            return true
        }
        return false
    }

}
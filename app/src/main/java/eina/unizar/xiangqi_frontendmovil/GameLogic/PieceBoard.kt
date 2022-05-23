package eina.unizar.xiangqi_frontendmovil.GameLogic

import java.util.*



class PieceBoard {

    var finnish = false
    var turnoDe = Color.ROJO
    var tablero = arrayOfNulls<Piece>(90)
    lateinit var generalRojo : Piece
    lateinit var generalNegro :Piece


    init{ generarPiezas()
        for (p in tablero) p!!.update()}

    private fun generarPiezas(){

        //rellenar con piezas nulas el tablero
        for (i in 0..89) addPiece(NullPiece(Coordinates(i/9, i%9),this))

        //generar generales
        generalRojo = General(Coordinates(0,4), this, Color.ROJO)
        generalNegro = General(Coordinates(9,4), this, Color.NEGRO)
        addPiece(generalNegro)
        addPiece(generalRojo)

        //generar oficiales(consejero)
        addPiece(Oficial(Coordinates(0,3), this, Color.ROJO))
        addPiece(Oficial(Coordinates(0,5), this, Color.ROJO))
        addPiece(Oficial(Coordinates(9,3), this, Color.NEGRO))
        addPiece(Oficial(Coordinates(9,5), this, Color.NEGRO))

        //generar elefantes
        addPiece(Elefante(Coordinates(0,2), this, Color.ROJO))
        addPiece(Elefante(Coordinates(0,6), this, Color.ROJO))
        addPiece(Elefante(Coordinates(9,2), this, Color.NEGRO))
        addPiece(Elefante(Coordinates(9,6), this, Color.NEGRO))

        //generar caballos
        addPiece(Caballo(Coordinates(0,1), this, Color.ROJO))
        addPiece(Caballo(Coordinates(0,7), this, Color.ROJO))
        addPiece(Caballo(Coordinates(9,1), this, Color.NEGRO))
        addPiece(Caballo(Coordinates(9,7), this, Color.NEGRO))

        //generan torres
        addPiece(Torre(Coordinates(0,0),this,Color.ROJO))
        addPiece(Torre(Coordinates(0,8),this,Color.ROJO))
        addPiece(Torre(Coordinates(9,0),this,Color.NEGRO))
        addPiece(Torre(Coordinates(9,8),this,Color.NEGRO))

        //generar canyones
        addPiece(Canyon(Coordinates(2,1),this, Color.ROJO))
        addPiece(Canyon(Coordinates(2,7),this, Color.ROJO))
        addPiece(Canyon(Coordinates(7,1),this, Color.NEGRO))
        addPiece(Canyon(Coordinates(7,7),this, Color.NEGRO))

        //generar peones
        for ( col in 0..8 step 2){
            addPiece(Soldado(Coordinates(3,col),this,Color.ROJO))
            addPiece(Soldado(Coordinates(6,col),this,Color.NEGRO))
        }

    }
    fun checkCheckMate(color: Color): Boolean{
        for(p in tablero)
            if(p!!.getColor() == color)
                for (cood in p.getMoves()){
                    if(p.canMove(cood)){
                        return false
                    }
                }
        return  true
    }

    fun checkCheck(color :Color) : Boolean{
        val atackedPositions = Vector<Coordinates>()
        for (p in tablero)
            if(p!!.getColor() != color)
                atackedPositions.addAll(p!!.getUpdateMoves())
        if (color == Color.NEGRO)
            return atackedPositions.contains(generalNegro.getPos())
        if (color == Color.ROJO)
            return atackedPositions.contains(generalRojo.getPos())
        else return false
    }

    fun update(){
        for (p in tablero) p!!.update()
        if (turnoDe == Color.ROJO) turnoDe = Color.NEGRO
        else turnoDe = Color.ROJO
    }

    fun getMoves(from : Coordinates) : List<Coordinates> {
        if(tablero[from.columna + from.fila * 9]!!.getColor() != turnoDe) return  listOf()
        return tablero[from.columna + from.fila * 9]!!.getMoves()
    }

    fun move(from : Coordinates, to : Coordinates) : Boolean {
        if(tablero[from.columna + from.fila * 9]!!.getColor() == turnoDe){
            return  tablero[from.columna + from.fila * 9]!!.moveTo(to)
        }
        return false
    }

    fun addPiece(pieza : Piece){
        val pos = pieza.getPos()
        tablero[pos.columna + pos.fila * 9] = pieza
    }

    fun getPiece(from :Coordinates): Piece {
        if(from.columna < 0 || from.columna > 8 || from.fila < 0 || from.fila > 9)
            return OutPiece(from, this)
        return tablero[from.columna + from.fila * 9]!!
    }

    fun getSide(from : Coordinates) : Color {
        if (from.fila <= 4){
            return Color.ROJO
        }
        if (from.fila >= 5){
            return Color.NEGRO
        }
        return Color.FUERA
    }

    fun inPalace(from : Coordinates, color : Color) : Boolean{
        if (from.columna < 3 || from.columna > 5 ) return false
        if (from.fila < 3 && color == Color.ROJO) return true
        if (from.fila > 6 && color == Color.NEGRO) return true
        return false
    }

    override fun toString(): String {
        return "hi"
    }
}
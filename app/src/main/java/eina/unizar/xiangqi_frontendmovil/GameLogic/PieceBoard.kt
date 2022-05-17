package eina.unizar.xiangqi_frontendmovil.GameLogic

import java.util.*



class PieceBoard {

    var finnish = false
    var turnoDe = Color.ROJO
    var tablero = Vector<Piece>()
    var canyons = Vector<Piece>()
    lateinit var generalRojo : Piece
    lateinit var generalNegro :Piece


    init{ generarPiezas() }

    private fun generarPiezas(){

        //rellenar con piezas nulas el tablero
        for (i in 0..89) addPiece(NullPiece(Coordinates(i%10, i/9),this))

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
        canyons.add(Canyon(Coordinates(2,1),this, Color.ROJO))
        canyons.add(Canyon(Coordinates(2,7),this, Color.ROJO))
        canyons.add(Canyon(Coordinates(7,1),this, Color.ROJO))
        canyons.add(Canyon(Coordinates(7,7),this, Color.ROJO))
        for (c in canyons) addPiece(c)

        //generar peones
        for ( col in 1..9 step 2){
            addPiece(Soldado(Coordinates(3,col),this,Color.ROJO))
            addPiece(Soldado(Coordinates(6,col),this,Color.NEGRO))
        }

    }
    fun checkCheckMate(color: Color): Boolean{
        for(p in tablero)
            if(p.getColor() == color)
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
            if(p.getColor() != color)
                atackedPositions.addAll(p.getUpdateMoves())
        if (color == Color.NEGRO)
            return atackedPositions.contains(generalNegro.getPos())
        if (color == Color.ROJO)
            return atackedPositions.contains(generalRojo.getPos())
        else return false
    }

    fun update(){
        for (p in tablero) p.update()
        if (turnoDe == Color.ROJO) turnoDe = Color.NEGRO
        else turnoDe = Color.ROJO
    }

    fun getMoves(from : Coordinates) : List<Coordinates> = tablero[from.columna + from.fila * 10].getMoves()

    fun move(from : Coordinates, to : Coordinates) : Boolean {
        if(tablero[from.columna + from.fila * 10].getColor() == turnoDe){
            return  tablero[from.columna + from.fila * 10].moveTo(to)
        }
        return false
    }

    fun addPiece(pieza : Piece){
        val pos = pieza.getPos()
        tablero[pos.columna + pos.fila * 10] = pieza
    }

    fun getPiece(from :Coordinates) = tablero[from.columna + from.fila * 10]

    override fun toString(): String {
        return "hi"
    }
}
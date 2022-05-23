package eina.unizar.xiangqi_frontendmovil.GameLogic

import kotlin.collections.List

class Caballo (pos : Coordinates, board: PieceBoard, color: Color): PieceImpl(pos, board, color){
    override fun getUpdateMoves(): List<Coordinates> {
        val fila = getPos().fila
        val columna = getPos().columna
        val moves : MutableList<Coordinates> = mutableListOf()

        //movimientos arriba
        if (board.getPiece(Coordinates(fila + 1,columna)).getColor() == Color.VACIO
            && board.getPiece(Coordinates(fila  +2, columna)).getColor() == Color.VACIO ){
            if(board.getPiece(Coordinates(fila + 2, columna + 1)).getColor() != Color.FUERA
                && board.getPiece(Coordinates(fila + 2, columna + 1)).getColor() != getColor()){
                moves.add(Coordinates(fila + 2, columna + 1))
            }
            if(board.getPiece(Coordinates(fila + 2, columna - 1)).getColor() != Color.FUERA
                && board.getPiece(Coordinates(fila + 2, columna - 1)).getColor() != getColor()){
                moves.add(Coordinates(fila + 2, columna - 1))
            }
        }

        //movimientos abajo
        if (board.getPiece(Coordinates(fila - 1,columna)).getColor() == Color.VACIO
            && board.getPiece(Coordinates(fila  -2, columna)).getColor() == Color.VACIO ){
            if(board.getPiece(Coordinates(fila - 2, columna + 1)).getColor() != Color.FUERA
                && board.getPiece(Coordinates(fila - 2, columna + 1)).getColor() != getColor()){
                moves.add(Coordinates(fila - 2, columna + 1))
            }
            if(board.getPiece(Coordinates(fila - 2, columna - 1)).getColor() != Color.FUERA
                && board.getPiece(Coordinates(fila - 2, columna - 1)).getColor() != getColor()){
                moves.add(Coordinates(fila - 2, columna - 1))
            }
        }
        //movimientos derecha
        if(board.getPiece(Coordinates(fila, columna + 1)).getColor() == Color.VACIO
            && board.getPiece(Coordinates(fila, columna + 2)).getColor() == Color.VACIO){
            if(board.getPiece(Coordinates(fila + 1, columna + 2)).getColor() != Color.FUERA
                && board.getPiece(Coordinates(fila + 1, columna + 2)).getColor() != getColor()){
                moves.add(Coordinates(fila + 1, columna + 2))
            }
            if(board.getPiece(Coordinates(fila - 1, columna + 2)).getColor() != Color.FUERA
                && board.getPiece(Coordinates(fila - 1, columna + 2)).getColor() != getColor()){
                moves.add(Coordinates(fila - 1, columna + 2))
            }
        }
        //movimientos izquierda
        if(board.getPiece(Coordinates(fila, columna - 1)).getColor() == Color.VACIO
            && board.getPiece(Coordinates(fila, columna - 2)).getColor() == Color.VACIO){
            if(board.getPiece(Coordinates(fila + 1, columna - 2)).getColor() != Color.FUERA
                && board.getPiece(Coordinates(fila + 1, columna - 2)).getColor() != getColor()){
                moves.add(Coordinates(fila + 1, columna - 2))
            }
            if(board.getPiece(Coordinates(fila - 1, columna - 2)).getColor() != Color.FUERA
                && board.getPiece(Coordinates(fila - 1, columna - 2)).getColor() != getColor()){
                moves.add(Coordinates(fila - 1, columna + 2))
            }
        }

        return moves
    }

    override fun getImgName(skin: Int): String? {
        var color = "negro"
        if(getColor() == Color.ROJO) color = "rojo"
        return "caballo$color$skin"
    }

}
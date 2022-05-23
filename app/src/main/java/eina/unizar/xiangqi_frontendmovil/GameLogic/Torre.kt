package eina.unizar.xiangqi_frontendmovil.GameLogic

class Torre (pos : Coordinates, board: PieceBoard, color: Color): PieceImpl(pos, board, color) {
    override fun getUpdateMoves(): List<Coordinates> {
        val fila = getPos().fila
        val columna = getPos().columna
        val moves : MutableList<Coordinates> = mutableListOf()

        var i = 0
        while(true){
            i++
            if ( board.getPiece(Coordinates(fila + i, columna)).getColor() == Color.VACIO)
                moves.add(Coordinates(fila+ i, columna))
            else{
                if( board.getPiece(Coordinates(fila + i, columna)).getColor() != Color.FUERA
                    && board.getPiece(Coordinates(fila + i, columna)).getColor() != getColor())
                    moves.add(Coordinates(fila + i, columna))
                break
            }

        }

        i = 0
        while (true){
            i--
            if ( board.getPiece(Coordinates(fila + i, columna)).getColor() == Color.VACIO)
                moves.add(Coordinates(fila+ i, columna))
            else{
                if( board.getPiece(Coordinates(fila + i, columna)).getColor() != Color.FUERA
                    && board.getPiece(Coordinates(fila + i, columna)).getColor() != getColor())
                    moves.add(Coordinates(fila + i, columna))
                break
            }
        }

        i = 0
        while(true){
            i++
            if ( board.getPiece(Coordinates(fila , columna+ i)).getColor() == Color.VACIO)
                moves.add(Coordinates(fila, columna + i))
            else{
                if( board.getPiece(Coordinates(fila, columna + i)).getColor() != Color.FUERA
                    && board.getPiece(Coordinates(fila, columna+ i)).getColor() != getColor())
                    moves.add(Coordinates(fila, columna+ i))
                break
            }

        }

        i = 0
        while(true){
            i--
            if ( board.getPiece(Coordinates(fila , columna+ i)).getColor() == Color.VACIO)
                moves.add(Coordinates(fila, columna + i))
            else{
                if( board.getPiece(Coordinates(fila, columna + i)).getColor() != Color.FUERA
                    && board.getPiece(Coordinates(fila, columna+ i)).getColor() != getColor())
                    moves.add(Coordinates(fila, columna+ i))
                break
            }

        }

        return moves
    }

    override fun getImgName(skin: Int): String? {
        var color = "negro"
        if(getColor() == Color.ROJO) color = "rojo"
        return "torre$color$skin"
    }

}
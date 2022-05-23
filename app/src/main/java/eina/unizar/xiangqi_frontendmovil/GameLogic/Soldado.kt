package eina.unizar.xiangqi_frontendmovil.GameLogic

class Soldado (pos : Coordinates, board: PieceBoard, color: Color): PieceImpl(pos, board, color) {
    override fun getUpdateMoves(): List<Coordinates> {
        val fila = getPos().fila
        val columna = getPos().columna
        val moves : MutableList<Coordinates> = mutableListOf()
        var i = 0

        if ( board.getPiece(Coordinates(fila + 1, columna)).getColor() != Color.FUERA
            && board.getPiece(Coordinates(fila + 1, columna)).getColor() != getColor()
            && getColor() == Color.ROJO){
            moves.add(Coordinates(fila + 1, columna))
        }

        if ( board.getPiece(Coordinates(fila - 1, columna)).getColor() != Color.FUERA
            && board.getPiece(Coordinates(fila - 1, columna)).getColor() != getColor()
            && getColor() == Color.NEGRO){
            moves.add(Coordinates(fila - 1, columna))
        }

        if ( board.getSide(Coordinates(fila,columna)) != getColor()){
            if(board.getPiece(Coordinates(fila, columna + 1)).getColor() != Color.FUERA
                && board.getPiece(Coordinates(fila, columna + 1)).getColor() != getColor()){
                    moves.add(Coordinates(fila, columna + 1))
            }
            if(board.getPiece(Coordinates(fila, columna - 1)).getColor() != Color.FUERA
                && board.getPiece(Coordinates(fila, columna - 1)).getColor() != getColor()){
                moves.add(Coordinates(fila, columna - 1))
            }
        }

        return moves
    }
    override fun getImgName(skin: Int): String? {
        var color = "negro"
        if(getColor() == Color.ROJO) color = "rojo"
        return "soldado$color$skin"
    }
}
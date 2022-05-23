package eina.unizar.xiangqi_frontendmovil.GameLogic

class Elefante (pos : Coordinates, board: PieceBoard, color: Color): PieceImpl(pos, board, color) {
    override fun getUpdateMoves(): List<Coordinates> {
        val fila = getPos().fila
        val columna = getPos().columna
        val moves : MutableList<Coordinates> = mutableListOf()
        var i = 0


        val posibleMoves = listOf<Coordinates>(
            Coordinates(fila + 2, columna + 2),
            Coordinates(fila - 2, columna + 2),
            Coordinates(fila + 2, columna - 2),
            Coordinates(fila - 2, columna - 2)
        )

        for (m in posibleMoves){
            if (board.getPiece(m).getColor() != Color.FUERA
                && board.getPiece(m).getColor() != getColor()
                && board.getSide(m) == getColor()){
                moves.add(m)
            }

        }
        return moves
    }

    override fun getImgName(skin: Int): String? {
        var color = "negro"
        if(getColor() == Color.ROJO) color = "rojo"
        return "elefante$color$skin"
    }

}
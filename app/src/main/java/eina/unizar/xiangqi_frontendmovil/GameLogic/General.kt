package eina.unizar.xiangqi_frontendmovil.GameLogic

class General (pos : Coordinates, board: PieceBoard, color: Color): PieceImpl(pos, board, color)  {
    override fun getUpdateMoves(): List<Coordinates> {
        val fila = getPos().fila
        val columna = getPos().columna
        val moves : MutableList<Coordinates> = mutableListOf()

        val posibleMoves = listOf<Coordinates>(
            Coordinates(fila + 1, columna),
            Coordinates(fila - 1, columna),
            Coordinates(fila, columna + 1),
            Coordinates(fila, columna - 1)
        )

        for (m in posibleMoves)
            if(board.inPalace(m,getColor())
                && board.getPiece(m).getColor() != getColor()
                && board.getPiece(m).getColor() != Color.FUERA)
                moves.add(m)

        return  moves
    }

    override fun getImgName(skin: Int): String? {
        var color = "negro"
        if(getColor() == Color.ROJO) color = "rojo"
        return "general$color$skin"
    }

}

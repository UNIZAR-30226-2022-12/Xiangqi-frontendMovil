package eina.unizar.xiangqi_frontendmovil.GameLogic

class NullPiece(pos : Coordinates, board: PieceBoard): PieceImpl(pos, board, Color.VACIO) {
    override fun getUpdateMoves(): List<Coordinates> {
        TODO("Not yet implemented")
    }

}
package eina.unizar.xiangqi_frontendmovil.GameLogic

class NullPiece(pos : Coordinates, board: PieceBoard): PieceImpl(pos, board, Color.VACIO) {
    override fun getUpdateMoves(): List<Coordinates> {
        return mutableListOf()
    }

    override fun getImgName(skin: Int): String? {
        return null
    }
}
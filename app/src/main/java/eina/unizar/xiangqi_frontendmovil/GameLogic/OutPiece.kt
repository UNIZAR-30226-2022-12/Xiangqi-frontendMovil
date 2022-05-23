package eina.unizar.xiangqi_frontendmovil.GameLogic

class OutPiece(pos : Coordinates, board: PieceBoard): PieceImpl(pos, board, Color.FUERA) {
    override fun getUpdateMoves(): List<Coordinates> {
        return mutableListOf()
    }
    override fun getImgName(skin: Int): String? {
        return  null
    }
}
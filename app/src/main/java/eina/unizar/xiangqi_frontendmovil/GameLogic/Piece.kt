package eina.unizar.xiangqi_frontendmovil.GameLogic

interface Piece {
    fun moveTo(to : Coordinates) : Boolean
    fun getMoves(): List<Coordinates>
    fun canMove(to : Coordinates) : Boolean
    fun getPos(): Coordinates
    fun update()
    fun getColor() : Color
    fun getUpdateMoves() : List<Coordinates>
}

enum class Color {
    ROJO, NEGRO, VACIO,
}

abstract class PieceImpl (private var pos : Coordinates, val board : PieceBoard, private val col : Color ): Piece {
    lateinit var mov : List<Coordinates>
    override fun getMoves(): List<Coordinates> {
        return mov
    }
    override fun getPos(): Coordinates {
       return pos
    }

    override fun getColor(): Color {
        return col
    }

    override fun update(){
        mov = getUpdateMoves()
    }

    override fun canMove(to: Coordinates) : Boolean{

        if(!mov.contains(to))return  false
        var can = true
        //movemos la pieza y nos guardamos la posicion anterio
        val auxPiece = board.getPiece(to)
        val auxCood = this.pos
        pos = to
        board.addPiece(this)
        board.addPiece(NullPiece(auxCood,board))

        //comprobamos que no este en jaque
        if (board.checkCheck(col)) can = false

        //volvemos a la posicion correspondiente
        pos = auxCood
        board.addPiece(this)
        board.addPiece(auxPiece)

        return can
    }
    override fun moveTo(to: Coordinates): Boolean {

        if(canMove(to)){
            val aux = NullPiece(Coordinates(pos.fila, pos.columna),board)
            pos = to
            board.addPiece(this)
            board.addPiece(aux)
            board.update()
            return true
        }

        return false
    }

}
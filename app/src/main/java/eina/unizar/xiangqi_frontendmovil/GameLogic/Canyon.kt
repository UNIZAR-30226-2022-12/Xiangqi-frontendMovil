package eina.unizar.xiangqi_frontendmovil.GameLogic

class Canyon (pos : Coordinates, board: PieceBoard, color: Color): PieceImpl(pos, board, color) {
    override fun getUpdateMoves(): List<Coordinates> {
        val fila = getPos().fila
        val columna = getPos().columna
        val moves : MutableList<Coordinates> = mutableListOf()
        var i = 0

        //arriba
        while (true) {
            i++
            if (board.getPiece(Coordinates(fila + i, columna)). getColor() == Color.VACIO){
                moves.add(Coordinates(fila + i, columna))
            }
            else if( board.getPiece(Coordinates(fila + i, columna)). getColor() == Color.FUERA)
                break
            else {
                while(true){
                    i++
                    if (board.getPiece(Coordinates(fila + i, columna)). getColor() != Color.VACIO){
                        if(board.getPiece(Coordinates(fila + i, columna)). getColor() != getColor()
                            && board.getPiece(Coordinates(fila + i, columna)). getColor() != Color.FUERA){
                            moves.add(Coordinates(fila + i, columna))
                        }
                        break
                    }
                }
                break
            }
        }


        //abajo
        i = 0
        while (true) {
            i--
            if (board.getPiece(Coordinates(fila + i, columna)). getColor() == Color.VACIO){
                moves.add(Coordinates(fila + i, columna))
            }
            else if( board.getPiece(Coordinates(fila + i, columna)). getColor() == Color.FUERA)
                break
            else {
                while(true){
                    i--
                    if (board.getPiece(Coordinates(fila + i, columna)). getColor() != Color.VACIO){
                        if(board.getPiece(Coordinates(fila + i, columna)). getColor() != getColor()
                            && board.getPiece(Coordinates(fila + i, columna)). getColor() != Color.FUERA){
                            moves.add(Coordinates(fila + i, columna))
                        }
                        break
                    }
                }
                break
            }
        }

        //derecha
        i = 0
        while (true) {
            i++
            if (board.getPiece(Coordinates(fila , columna + i)). getColor() == Color.VACIO){
                moves.add(Coordinates(fila, columna + i))
            }
            else if( board.getPiece(Coordinates(fila, columna + i)). getColor() == Color.FUERA)
                break
            else {
                while(true){
                    i++
                    if (board.getPiece(Coordinates(fila, columna + i)). getColor() != Color.VACIO){
                        if(board.getPiece(Coordinates(fila, columna + i)). getColor() != getColor()
                            && board.getPiece(Coordinates(fila, columna + i)). getColor() != Color.FUERA){
                            moves.add(Coordinates(fila, columna + i))
                        }
                        break
                    }
                }
                break
            }
        }

        //izquierda
        i = 0
        while (true) {
            i--
            if (board.getPiece(Coordinates(fila , columna + i)). getColor() == Color.VACIO){
                moves.add(Coordinates(fila, columna + i))
            }
            else if( board.getPiece(Coordinates(fila, columna + i)). getColor() == Color.FUERA)
                break
            else {
                while(true){
                    i--
                    if (board.getPiece(Coordinates(fila, columna + i)). getColor() != Color.VACIO){
                        if(board.getPiece(Coordinates(fila, columna + i)). getColor() != getColor()
                            && board.getPiece(Coordinates(fila, columna + i)). getColor() != Color.FUERA){
                            moves.add(Coordinates(fila, columna + i))
                        }
                        break
                    }
                }
                break
            }
        }

        return  moves
    }

    override fun getImgName(skin: Int): String? {
        var color = "negro"
        if(getColor() == Color.ROJO) color = "rojo"
        return "canyon$color$skin"
    }

}
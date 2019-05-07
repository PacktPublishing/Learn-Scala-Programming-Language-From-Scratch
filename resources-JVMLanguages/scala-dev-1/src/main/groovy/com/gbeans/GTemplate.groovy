



package com.gbeans

class GTemplate implements  MathRunner  {

    public def multiply = { x, y -> return x * y }

    def k

    @Override
    int multiply(int x, int y) {
        def q = multiply.call(x, y)
        return q
    }
}

interface MathRunner {
    public int multiply(int x, int y ) ;
}



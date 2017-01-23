


if __name__ == '__main__':
    from Matrix import Matrix
    a=Matrix(3,4)
    print a
    #print a[0]
    #print a[0][0]
    #print a[5]
    #print 'going to reset value'
    #a[0][0] = 5
    #print a
    #print 'resetted value'
    print 'create square IdentityMatrix b'
    b = a.IdentityMatrix(5,5)
    print b
    c= a.IdentityMatrix(5,5)
    #print 'test equality of '+str(b)+' and '+str(c)
    #print str(c==b)
    #print 'Test dup return type:'+str(isinstance(b.dup(),Matrix))
    print 'Test rref:'
    d = Matrix(3,3)
    d[0][0] = 2
    d[0][1] = 3
    d[0][2] = 4
    d[1][0] = 5
    d[1][1] = 6
    d[1][2] = 7
    d[2][0] = 4
    d[2][1] = 6
    d[2][2] = 8


    
    
    print d
    r = d.rowEchelon()
    print d
    print r

    from Matrix import Matrix
    e = Matrix(3,3)
    e[0][0] = 1
    e[0][1] = 0
    e[0][2] = 0
    e[1][0] = -5
    e[1][1] = 1
    e[1][2] = 0
    e[2][0] = 0
    e[2][1] = 0
    e[2][2] = 1

    f = Matrix(3,3)
    f[0][0] = 1
    f[0][1] = 0
    f[0][2] = 0
    f[1][0] = 5
    f[1][1] = 1
    f[1][2] = 0
    f[2][0] = 0
    f[2][1] = 0
    f[2][2] = 1

    i = e.inverse()
    

    print e


    from Matrix import Matrix
    g = Matrix(3,4)
    g.fill(2.5)

    from Matrix import Matrix
    e = Matrix(3,3)
    e[0][0] = 1
    e[0][1] = 1
    e[0][2] = 1
    e[1][0] = 2
    e[1][1] = 2
    e[1][2] = 2
    e[2][0] = 3
    e[2][1] = 3
    e[2][2] = 3

    f = Matrix(3,3)
    f[0][0] = 2.1
    f[0][1] = 2.2
    f[0][2] = 2.3
    f[1][0] = 3.1
    f[1][1] = 3.2
    f[1][2] = 3.3
    f[2][0] = 3.4
    f[2][1] = 3.5
    f[2][2] = 3.6

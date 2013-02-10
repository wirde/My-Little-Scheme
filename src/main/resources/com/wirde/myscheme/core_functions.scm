(define print display)
(define (newline) (print "
"))

(define caar (lambda (l) (car (car l))))
(define cadr (lambda (l) (car (cdr l))))

(define list (lambda x x))

(define null? (lambda (x)
	(if (eq? nil x)
		#t
		#f)))

(define (for-each f l)
	(if (null? l)
		nil
		(begin
			(f (car l))
			(for-each f (cdr l)))))		
		
(define (reduce f l initial) 
	(if (null? l)
		initial
		(f (car l) (reduce f (cdr l) initial))))

(define (map f l)
	(if (null? l)
		nil
		(cons (f (car l)) (map f (cdr l)))))
		
(define (filter f l)
	(if (null? l)
		nil
		(if (f (car l))
			(cons (car l) (filter f (cdr l)))
			(filter f (cdr l))))) 

(define (ass obj l pred)
	(if (null? l)
		#f
		(if (pred obj (caar l))
			(car l)
			(ass obj (cdr l) pred))))
			
(define (assq obj l)
	(ass obj l eq?))
	
(define (assv obj l)
	(ass obj l eqv?))	

(define (assoc obj l)
	(ass obj l equal?))
	
(define (mem obj l pred)
	(if (null? l)
		#f
		(if (pred obj (car l))
			l
			(mem obj (cdr l) pred))))

(define (memq obj l)
	(mem obj l eq?))
	
(define (memv obj l)
	(mem obj l eqv?))

(define (member obj l)
	(mem obj l equal?))

(define (list-ref l idx)
	(if (= idx 0)
		(car l)
		(list-ref (cdr l) (- idx 1))))

(define (expt x y)
	(define (expt-int res y)
		(if (= x 0)
			(if (= y 0)
				1
				0)
			(if (= y 1)
				res
				(expt-int (* res x) (- y 1)))))
	(expt-int x y))

(define (zero? x)
	(if (= x 0)
		#t
		#f))

(define (positive? x)
	(if (> x 0)
		#t
		#f))

(define (negative? x)
	(if (< x 0)
		#t
		#f))

(define (odd? x)
	(if (zero? (remainder x 2))
		#f
		#t))

(define (even? x)
	(if (zero? (remainder x 2))
		#t
		#f))

(define (reverse l)
	(define (reverse-iter new-list old-list)
		(if (null? old-list)
			new-list
			(reverse-iter (cons (car old-list) new-list) (cdr old-list))))
	(reverse-iter '() l))

;;Dummy functions
(define (append l1 l2)
	#f)
(define (rational? obj)
	#f)
(define (real? obj)
	#f)
(define (complex? obj)
	#f)
(define (vector? obj)
	#f)
(define (char? obj)
	#f)
(define (symbol? obj)
	#f)

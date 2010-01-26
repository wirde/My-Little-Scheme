(define print display)
(define (newline) (print "
"))

(define caar (lambda (l) (car (car l))))
(define cadr (lambda (l) (car (cdr l))))

(define list (lambda x x))

(define nil? (lambda (x)
	(if (eq? nil x)
		#t
		#f)))

(define (for-each f l)
	(if (nil? l)
		nil
		(begin
			(f (car l))
			(for-each f (cdr l)))))		
		
(define (reduce f l initial) 
	(if (nil? l)
		initial
		(f (car l) (reduce f (cdr l) initial))))

(define (map f l)
	(if (nil? l)
		nil
		(cons (f (car l)) (map f (cdr l)))))
		
(define (filter f l)
	(if (nil? l)
		nil
		(if (f (car l))
			(cons (car l) (filter f (cdr l)))
			(filter f (cdr l))))) 

(define (ass obj l pred)
	(if (nil? l)
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
	(if (nil? l)
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
	
;;Dummy functions
(define (inexact? obj)
	#f)
(define (exact? obj)
	#f)
(define (integer? obj)
	#f)
(define (rational? obj)
	#f)
(define (real? obj)
	#f)
(define (complex? obj)
	#f)
(define (number? obj)
	#f)
(define (boolean? obj)
	#f)
(define (symbol? obj)
	#f)
(define (list? obj)
	#f)
(define (pair? obj)
	#f)	
(define (expt x y)
	#f)		
(define (append obj l)
	#f)		
(define (reverse l)
	#f)
(define (list-ref l obj)
	#f)

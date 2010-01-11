(define display print)
(define write print)
(define (newline) (print "
"))

(define cadr (lambda (l) (car (cdr l))))

(define list (lambda x x))

(define nil? (lambda (x)
	(if (equal? nil x)
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

;;(let ((x 1) (y 2))
;;forms
;;)

;;((lambda (x y)
;;	forms
;;) 1 2)

;;(define let (lambda (bindings . forms)
;;	((lambda (x y)
;;		(car forms)
;;	) 1 2)))
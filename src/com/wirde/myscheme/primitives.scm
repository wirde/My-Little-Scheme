(define display print)

(define cadr (lambda (l) (car (cdr l))))

(define list (lambda x x))

(define nil? (lambda (x)
	(if (= nil x)
		#t
		#f)))
		
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
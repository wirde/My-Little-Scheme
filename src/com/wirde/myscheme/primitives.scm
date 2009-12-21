(define display print)
(define cadr (lambda (l) (car (cdr l))))

(define list (lambda x x))

(define nil? (lambda (x)
	(if (= nil x)
		#t
		#f)))
		
(define reduce (lambda (f l initial) 
	(if (nil? l)
		initial
		(f (car l) (reduce f (cdr l) initial)))))

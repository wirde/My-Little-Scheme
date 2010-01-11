;;Tests, baby steps...

(define nrOK 0)
(define nrFail 0)

(define test (lambda (expected exp)
	(if (equal? expected exp)
		#t
		#f)))

(test 5 (+ 1 2 (+ 1 2) (- 1 2)))
(test -5 (- 5))
(test -5 (- 5 10))
(test 98 (* 7 7 2))

(define (fac n) (if (= n 0) 1 (* n (fac (- n 1)))))

(test 24 (fac 4))

(define first (cons (- 2 1) nil))
(test (cons 1 nil) first)

(define three (cons 3 (cons 2 first)))
(test (cons 3 (cons 2 (cons 1 nil))) three)

(test 3 (car three))
(test (cons 2 (cons 1 nil)) (cdr three))

(test 2 (cadr three))

(test #f (= 1 0))
(test #t (= 1 1))
(test #f (equal? #t #f))
(test #t (equal? #t #t))

(test #t (equal? + +))

(define x 1)

(define (f) x)
(define g (lambda (f x) (f)))
(test 1 (f))
(test 1 (g f 2))

(test 10 (reduce + (list 1 2 3 4) 0))
(ns localweather.macros)

(defmacro cf-toggle [type a b]
  `(condp = ~type
    :c ~a
    :f ~b))



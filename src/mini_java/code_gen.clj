(ns mini-java.code-gen
  (:import [org.objectweb.asm
            ClassWriter Opcodes Type]
           [org.objectweb.asm.commons
            GeneratorAdapter Method]))

(defn byte-codes [class-table]
  (let [cw (ClassWriter. ClassWriter/COMPUTE_FRAMES)
        init (Method/getMethod "void <init>()")
        main (Method/getMethod "void main(String[])")
        init-gen (GeneratorAdapter. Opcodes/ACC_PUBLIC init nil nil cw)
        main-gen (GeneratorAdapter. (+ Opcodes/ACC_PUBLIC Opcodes/ACC_STATIC)
                                    main nil nil cw)]
    (.visit cw
            Opcodes/V1_1 Opcodes/ACC_PUBLIC "Example" nil
            "java/lang/Object" nil)
    (doto init-gen
      (.loadThis)
      (.invokeConstructor (Type/getType Object) init)
      (.returnValue)
      (.endMethod))

    (doto main-gen
      (.getStatic (Type/getType System)
                  "out"
                  (Type/getType java.io.PrintStream))
      (.invokeVirtual (Type/getType java.io.PrintStream)
                      (Method/getMethod "void println(String)"))
      (.returnValue)
      (.endMethod))

    (.visitEnd cw)

    {"Example" (.toByteArray cw)}))

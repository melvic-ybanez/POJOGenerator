package pojogenerator

class POJOGenerator private(
  className: String, 
  fields: List[(String, String)], 
  tabSize: Int,
  includeConstructors: Boolean, 
  overrideToString: Boolean, 
  overrideEquals: Boolean) {
  
  def generatePOJO = {
    def accessors(f: (String, String) => String) = 
      fields.map(field => f(field._1, field._2)).mkString("\n\n")
    
    val classDeclaration = "public class " + className + " {"
    val setters = accessors(genSetter)
    val getters = accessors(genGetter)
    val closingCurlyBrace = "} // end of class " + className
    
    val pojoList = classDeclaration :: genFields :: 
      (if (includeConstructors) genEmptyConstructor else "") ::
      (if (includeConstructors) genConstructorWithFields else "") :: 
      getters :: setters ::
      (if (overrideToString) genToString else "" ) ::
      (if (overrideEquals) genEquals else "") :: 
      closingCurlyBrace :: Nil
    
    pojoList.filter(_ != "").mkString("\n\n")
  }
  
  def genMethod(
    name: String, 
    parameters: List[String] = Nil, 
    body: List[String] = Nil, 
    optReturnType: Option[String] = Some("void"), 
    isConstructor: Boolean = false, 
    optAnnotation: Option[String] = None) = {
    
    val additionalSpace = if (isConstructor) "" else " "
    val bodyStr = body.map(x => tab + tab + x + ";").mkString("\n")
    val parameterString = parameters.mkString(", ")
    val annotation = optAnnotation.map(tab + "@" + _ + " ").getOrElse(tab)
    val returnType = optReturnType.getOrElse("")
    s"${annotation}public $returnType$additionalSpace$name($parameterString) {\n$bodyStr\n$tab}"
  } 
  
  def tab = " " * tabSize
  
  def genFields = fields.map(field => tab + "private " + field._1 + " " +field._2 + ";").mkString("\n")
  
  def genEmptyConstructor = s"${tab}public $className() { }"
  
  def genConstructorWithFields = genMethod(
    name = className,
    parameters = fields.map(field => field._1 + " " + field._2),
    body = fields.map(field => s"this.${field._2} = ${field._2}"),
    isConstructor = true,
    optReturnType = None)
  
  def genSetter(fieldType: String, fieldName: String) = genMethod(
    name = "set" + capFieldName(fieldName),
    parameters = fieldType + " " + fieldName :: Nil,
    body = s"this.$fieldName = $fieldName" :: Nil)
    
  def genGetter(fieldType: String, fieldName: String) = genMethod(
    name = (if (fieldType == "boolean" || fieldType == "Boolean") "is" else "get") + 
      capFieldName(fieldName),
    optReturnType = Some(fieldType),
    body = "return " + fieldName :: Nil)
    
  def capFieldName(fieldName: String) = fieldName.head.toUpper + fieldName.tail
    
  def genToString = {
    def xs = fields.map(field => s"""${field._2}=" + ${field._2}""")
      .mkString("\n" + tab + tab + tab + "+ \", ")
    genMethod(
      name = "toString",
      optReturnType = Some("String"),
      optAnnotation = Some("Override"),
      body = s"""return "$className [$xs + "]"""" :: Nil)
  }
  
  def genEquals = genMethod(
    name = "equals",
    parameters = "Object obj" :: Nil,
    optReturnType = Some("boolean"),
    optAnnotation = Some("Override"),
    body = {
      val resultDeclaration = "boolean equals = false"
      val instanceValidation = s"if (obj instanceof $className) {"
      val thatInstance = s"$className that = ($className) obj"
      val fieldComparisons = fields.map { field => 
        val thisField = s"this.${field._2}"
        val thatField = s"that.get${capFieldName(field._2)}()"
        if (field._1.head.isUpper) s"$thisField.equals($thatField)"
        else s"$thisField == $thatField"
      }
      val checkEquals = "equals = " + fieldComparisons.mkString(" &&\n" + tab + tab + tab + tab)
      val returnStatement = "return equals"
      List(
        resultDeclaration,
        s"\n$tab$tab$instanceValidation\n$tab$tab$tab$thatInstance",
        tab + checkEquals,
        s"}\n\n$tab$tab$returnStatement")
    })
}

object POJOGenerator {
  def apply(
    className: String, 
    fields: List[(String, String)],
    tabSize: Int,
    includeConstructors: Boolean,
    overrideToString: Boolean, 
    overrideEquals: Boolean) = 
      new POJOGenerator(
        className, fields, tabSize, includeConstructors,
        overrideToString, overrideEquals).generatePOJO
}
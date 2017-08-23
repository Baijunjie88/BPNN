object NeuronNetwork {
	var layers:List[Layer] = List()
	var totalError = 0.0
	
	def init(){
		buildLayers()
	}
	
	def buildLayers(){
	  	val inputLayer = new Layer(Controller.inputDim,0)
		layers = layers:+inputLayer
		
		for (i <- 0 until Controller.hiddenLayerCount){
		  layers = layers :+ new Layer(Controller.hiddenDims(i), layers.length)
		}
		
		val outputLayer = new Layer(Controller.outputDim, layers.length)
		layers = layers:+outputLayer
	}
	
	def execute(input:Array[Double]){
	  var layerInput = input
	  //forward���L�{
	  for (i <- 0 until layers.length){
	    //println("forward time:" + System.currentTimeMillis())
	    layers(i).activate(layerInput)
	    layerInput = layers(i).getLayerOutput()
	  }
	}
	
	//�p��X�C��Neuron��Error��weight���L�� & ��threshold���L������
	def learn(input:Array[Double], target:Array[Double]){
	  execute(input)
	  
	  //�p��ErrorFunction(Z)
	  var currentError = 0.0
	  for (i <- 0 until Controller.outputDim){
	    val output:Double = layers(layers.length-1).neurons(i).stateResult
	    val desiredTarget:Double = target(i)
	    currentError = currentError + 0.5 * Math.pow(output - desiredTarget, 2)
	  }
	  totalError = totalError + currentError
	  
	  /*
	  print("desired target:")
	  for (i <- 0 until target.length){
	    print(target(i) + " ")
	  }
	  println()
	  */
	  
	  //backward
	  for (i <- layers.length-1 to 1 by -1){
	    //println("backward time:" + System.currentTimeMillis())
	    layers(i).propogate(target)
	  }
	  

	}
	
	//�վ�U��Neuron��weight�Pthreshold����
	def updateZ(){
	  layers.par.foreach((p:Layer)=>p.update)
	}
	
	def testWeights_Threshold(){
	  println("-------------------------Debug-------------------------")
	  for (i <- 1 until layers.length){
	    layers(i).printWeight_Threshold()
	    //var waitPoint = readLine("�п�J���N���~��")
	  }
	}
	
	def testError(){
	  println("-------------------------Check Error-------------------------")
	  	for (i <- 1 until layers.length){
	  		layers(i).printError()
	    var waitPoint = readLine("�п�J���N���~��")
	  }
	}
	
	def refresh(){
	  totalError = 0
	  layers.par.foreach((p:Layer)=>p.refresh())
	}
}
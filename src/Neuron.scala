class Neuron(val layerIndex:Int, val neuronIndex:Int) {
	//�C��Neuron���Ӧs���U�h�C��dimension��weight
    var weights:Array[Double] = {
      	if (layerIndex==0) Array.tabulate(Controller.inputDim)(i => 1)
      	else Array.tabulate(NeuronNetwork.layers(layerIndex-1).dim)(i => Controller.wLowerBound + (Controller.wUpperBound-Controller.wLowerBound)*scala.util.Random.nextDouble())
      	}
    var threshold:Double = {
      	if (layerIndex==0) 0
      	else Controller.wLowerBound + (Controller.wUpperBound-Controller.wLowerBound)*scala.util.Random.nextDouble()
    }
	var stateResult = 0.0
	//�ΨӼȦs��Neuron��C���~�b��E��Error��
	var errors_Weight:Array[Array[Double]]={	//�~�h�G��U�hNeuron /���h�G��C��Input Pattern
	  if (layerIndex!=0) Array.tabulate(NeuronNetwork.layers(layerIndex-1).dim)(i=>new Array[Double](Controller.learningData.length))
	  else Array[Array[Double]]()
	}
  	var errors_Threshold:Array[Double]=new Array[Double](Controller.learningData.length)	//��C��Input Pattern
  	
  	//�ΨӼȦs��Neuron������~�b��E�Ҳֿn�W�[���`Error��
  	var errorSum_Weight:Array[Double]={
	  if (layerIndex!=0) Array.tabulate(NeuronNetwork.layers(layerIndex-1).dim)(i=>0.0)
	  else Array[Double]()
	}
  	var errorSum_Threshold:Double=0.0
  	
  	//�ഫ���
    var transformFunc:Double=>Double = {
      def sigmoid(sum:Double):Double={
        //for check
        //if (Math.exp(-1*sum).isNaN()) println("exp(-1*x) is NaN!!!\tsum:" + sum)
        var exp = Math.exp(-1*sum)
        var sigResult = 1.0/(1.0+exp)
        if (exp.isNaN() && sum>=0) sigResult = 1
        else if (exp.isNaN() && sum < 0) sigResult = 0
        //else if (sum.isNaN()) println("Sum is NaN!!!")
        sigResult
      }
      
      def hyperTangent(sum:Double):Double={
        //for check
        //if (Math.exp(-2*sum).isNaN()) println("exp(-2*x) is NaN!!!\tsum:" + sum )
        var exp = Math.exp(-2*sum)
        var tanResult = (1.0-exp)/(1.0+exp)
        if (exp.isNaN() && sum>=0) tanResult = 1
        else if (exp.isNaN() && sum < 0)  tanResult = -1
        //else if (sum.isNaN()) println("Sum is NaN!!!")
        tanResult
      }
      if (Controller.transformMethodChoice==1) sigmoid
      else hyperTangent
    }
	
    def calculateStateResult(input:Array[Double]){
      var sum:Double = threshold
      for (i <- 0 until NeuronNetwork.layers(layerIndex-1).dim){
        
        //check hiddenLayer at first input
//        if (layerIndex!=0 && 
//            layerIndex!=NeuronNetwork.layers.length-1 &&
//            Controller.learnGeneration<=20){
//	        if (input(i).isNaN())	println("input is NaN!!!!" + Controller.learnGeneration)
//	        if (weights(i).isNaN()) println("weight is NaN!!!!" + Controller.learnGeneration)
//        }
        sum = sum + weights(i) * input(i)
        if (sum > 4) sum = 4
        else if (sum < -4) sum = -4
      }
	  stateResult = transformFunc(sum)
	  if (stateResult.isNaN()) {
	    //println("stateResult is NaN")
	    stateResult = 0
	  }
	}
    
    def calculateError(target:Array[Double]){
      //outputLayer
      if (layerIndex==NeuronNetwork.layers.length-1){
    	  //�p��threshold��Error
    	  val currentThresholdError = (stateResult-target(neuronIndex))*
    			  (1.0-Math.pow(stateResult,2))
    			  
    	  errors_Threshold(Controller.learnDataIndex) = currentThresholdError
    	  
    	  //if (currentThresholdError.isNaN()) println("OutputLayer currentThresholdError equals Nan!!!" + Controller.learnGeneration)
//    	  println("OutputLayer currentThresholdError(" + Controller.learnDataIndex +
//    	      "): " + currentThresholdError)
    	  errorSum_Threshold = errorSum_Threshold + currentThresholdError
    	  //�p��C����weight��Error
    	  for (i <- 0 until NeuronNetwork.layers(layerIndex-1).dim){
    	    //���o�U�h���üh�Y�S�wNeuron���E�o���A��
    	    val downLayerOutput =  NeuronNetwork.layers(layerIndex-1).neurons(i).stateResult
    	    val currentWeightError = currentThresholdError * downLayerOutput
    	    errors_Weight(i)(Controller.learnDataIndex) = currentWeightError
    	    
    	    //if (currentWeightError.isNaN()) println("OutputLayer currentWeightError equals Nan!!!" + Controller.learnGeneration)
//    	    println("OutputLayer currentWeightError(" + i + "," 
//    	        + Controller.learnDataIndex + "): " + currentWeightError)
    	    errorSum_Weight(i) = errorSum_Weight(i) + currentWeightError
    	  }
    	  
      }	//hiddenLayer
      else if (layerIndex!=0){
        val upLayer = NeuronNetwork.layers(layerIndex+1)
        val downLayer = NeuronNetwork.layers(layerIndex-1)
        //�p��threshold��Error
        var currentThresholdError = 0.0
        for (i <- 0 until upLayer.dim){
          //println("Controller.learnDataIndex:" + Controller.learnDataIndex)
          val upLayerThresholdError = upLayer.neurons(i).errors_Threshold(Controller.learnDataIndex)
          val upLayerWeight = upLayer.neurons(i).weights(neuronIndex)
          currentThresholdError = currentThresholdError +
            upLayerThresholdError * 
            	(1.0 - Math.pow(stateResult, 2)) *
            		upLayerWeight
        }
        errors_Threshold(Controller.learnDataIndex) = currentThresholdError
        
       // if (currentThresholdError.isNaN()) println("HiddenLayer currentThresholdError equals Nan!!!" + Controller.learnGeneration)
//        println("HiddenLayer currentThresholdError(" + Controller.learnDataIndex +
//    	      "): " + currentThresholdError)
    	errorSum_Threshold = errorSum_Threshold + currentThresholdError
    	
    	//�p��C����weight��Error
    	for (j <- 0 until downLayer.dim){
    		val downLayerOutput = downLayer.neurons(j).stateResult
    		val currentWeightError = currentThresholdError * downLayerOutput
    		errors_Weight(j)(Controller.learnDataIndex) = currentWeightError
    		
    		//if (currentWeightError.isNaN()) println("HiddenLayer currentWeightError equals Nan!!!" + Controller.learnGeneration)
//    	    println("HiddenLayer currentWeightError(" + j + "," 
//    	        + Controller.learnDataIndex + "): " + currentWeightError)
    	    errorSum_Weight(j) = errorSum_Weight(j) + currentWeightError
        }
      }
    }
    
    def updateWeight_Threshold(){
      if (layerIndex!=0)
      {
	      val deltaThreshold = -1 * Controller.learnGain * errorSum_Threshold
	      threshold = threshold + deltaThreshold
	      for (i <- (0 until weights.length).par){
	        val deltaWeight = -1 * Controller.learnGain * errorSum_Weight(i)
	        weights(i) = weights(i) + deltaWeight
	      }
      }
    }
    
    def printWeight_Threshold(){
      print("< Threshold:" + threshold + " ")
      if (threshold.isNaN) println("threshold is NaN!!!!")
      for (i <- 0 until weights.length){
        if (weights(i).isNaN) println("weight is NaN!!!!")
        print("w" + (neuronIndex+1) + "/" + (i+1) + ":" + weights(i) + " ")
      }
      print(">")
    }
    
     def printError(){
      for (j <- 0 until Controller.learningData.length)
      {
	      print("< TError+" + (j+1) + ":" + this.errors_Threshold(j) + " ")
	      for (i <- 0 until weights.length){
	        print("wError" + (neuronIndex+1) + "/" + (i+1) + ":" + this.errors_Weight(i)(j) + " ")
	      }
	      println(">")
      }
    }
    
    def refresh(){
	    errors_Weight={
		  if (layerIndex!=0) Array.tabulate(NeuronNetwork.layers(layerIndex-1).dim)(i=>new Array[Double](Controller.learningData.length))
		  else Array[Array[Double]]()
		}
	  	errors_Threshold=new Array[Double](Controller.learningData.length)
	  	
	  	//�ΨӼȦs��Neuron������~�b��E�Ҳֿn�W�[���`Error��
	  	errorSum_Weight={
		  if (layerIndex!=0) Array.tabulate(NeuronNetwork.layers(layerIndex-1).dim)(i=>0.0)
		  else Array[Double]()
		}
	  	errorSum_Threshold=0.0
    }
}
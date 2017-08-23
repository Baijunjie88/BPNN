class Layer(val dim:Int, val index:Int){
	val neurons:Array[Neuron] = buildNeurons()
	
	//�إ�Layer���U��Neuron�PWeights
	def buildNeurons():Array[Neuron] = {
	  var temp:Array[Neuron] = new Array[Neuron](dim)
	  //2014/6/22 �s�W����
	  for (i <- (0 until dim).par){
	    temp(i) = new Neuron(index,i)
	  }
	  temp
	}
	
	//�p��E�o���A��
	def activate(input:Array[Double]){
	  if (index==0){
	    for (i <- 0 until neurons.length){
	      neurons(i).stateResult = input(i)
	    }
	  }
	  else neurons.par.foreach((p:Neuron)=>p.calculateStateResult(input))
	}
	
	//����Layer��Neuron���E�o���A�Ȫ��զX������W�@��Layer��input
	def getLayerOutput():Array[Double] = {
	  //printLayerOutput()
	  Array.tabulate(neurons.length)(i => neurons(i).stateResult)
	}
	
	def printLayerOutput(){
	  for (i <- 0 until dim){
	    print(neurons(i).stateResult + "\t")
	  }
	  println()
	}
	
	def propogate(target:Array[Double]){
	  neurons.par.foreach((p:Neuron)=>p.calculateError(target))
	}
	
	def update(){
	   neurons.par.foreach((p:Neuron)=>p.updateWeight_Threshold)
	}
	
	def printWeight_Threshold(){
	  for (i <- 0 until dim){
	    neurons(i).printWeight_Threshold()
	    var waitPoint = readLine("�п�J���N���~��")
	  }
	  println()
	}
	
	def printError(){
	  for (i <- 0 until dim){
	    neurons(i).printError()
	  }
	  println()
	}
	
	def refresh(){
	  neurons.par.foreach((p:Neuron)=>p.refresh)
	}
}
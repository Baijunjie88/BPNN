object Controller {
	var inputDim = 0
	var hiddenLayerCount = 1
	var hiddenDims:Array[Int] = Array()
	var outputDim = 0
	var wUpperBound = 0.0
	var wLowerBound = 0.0
	var transformMethodChoice:Int = 2
	
	var learningData:Vector[Array[Double]] = Vector[Array[Double]]()
    var desiredTargets:Vector[Array[Double]] = Vector[Array[Double]]()
	var learnGain = 0.0
    var executingData:Vector[Array[Double]] = Vector[Array[Double]]()
    var learnDataIndex:Int = 0
    var learnGeneration:Int = 0
    
	def main(args: Array[String]) {
		inputDim = readLine("�п�JInput�h�����סG").toInt
		//hiddenLayerCount = readLine("�п�JHidden�h���h�ơG").toInt
		hiddenDims = new Array[Int](hiddenLayerCount)
		for (i <- 0 until hiddenLayerCount){
		  hiddenDims(i) = readLine("�п�JHidden�h��" + (i+1) + "�h�����סG").toInt
		}
		outputDim = readLine("�п�JOutput�h�����סG").toInt
        wUpperBound = readLine("�п�JNeuron Weight���W�ɭȡG").toDouble
        wLowerBound = readLine("�п�JNeuron Weight���U�ɭȡG").toDouble
		//transformMethodChoice = readLine("�п���ഫ���\n1.���s��(Sigmoid)���\n2. ��������(Hyperbolic-tangent)���\n").toInt
		learnGain = readLine("�п�J�ǲ߳t�v�ȡG").toDouble
		
		//Ū���ǲ߸��
		for(line <- scala.io.Source.fromFile("Learn_Data.txt").getLines()){
	      //for testing
		  //println("readline:" + line)
		  if (line.length>2)
		  {
			  val lineSplit = line.split("/")
			  val learnSplit = lineSplit(0).split(" ")
			  val targetSplit = lineSplit(1).split(" ")
			  
			  //println("lineSplit:" + lineSplit.length)
			  //println("learnSplit:" + learnSplit.length)
			  //println("targetSplit:" + targetSplit.length)
			 
	    	  learningData = learningData:+learnSplit.map(_.toDouble)  
	    	  desiredTargets = desiredTargets:+targetSplit.map(_.toDouble)  
	    	  //var waitPoint = readLine("��J�H�N���~��")
		  }
		}   
		
		//Ū��������
		for(line <- scala.io.Source.fromFile("Execute_Data.txt").getLines()){
	      val lineSplit = line.split(" ")
    	  executingData = executingData:+lineSplit.map(_.toDouble)  
		} 
		
		//�إ������g����
		NeuronNetwork.init()
		
		//���եΡA�[�������g�����̪쪺���A
		//NeuronNetwork.testWeights_Threshold()
		
		//�ǲ߶��q
		learnGeneration = 0
		var preTotalError = -100.0
		do
		{
		    val startTime = System.currentTimeMillis()
		    NeuronNetwork.refresh()
		    learnGeneration = learnGeneration + 1
			println("-------------------------Learning "+ learnGeneration +"-------------------------")
		    //c�ݩ�k
		    //2014/6/22 �s�W����
		    for(i <- (0 until learningData.length).par){
	          learnDataIndex = i
	          //println("<InputLayer>")
	          NeuronNetwork.learn(learningData(i),desiredTargets(i))	          
	        }
		    println("E(Z):" + NeuronNetwork.totalError)
		    modifyLearnGain(NeuronNetwork.totalError, preTotalError)
		    if (NeuronNetwork.totalError>Math.pow(10.0,-3)){
		      //println("Update!!!")
		      NeuronNetwork.updateZ()
		    }
		    
			//���եΡA�[��ǲߤ��������g���������A
			//NeuronNetwork.testWeights_Threshold()
			
		    //���եΡA�[��C��Neruon��Error��
		    //NeuronNetwork.testError()
		    
		    val endTime = System.currentTimeMillis()
		    preTotalError = NeuronNetwork.totalError
		    println("Time Cost: " + (endTime-startTime))
		}while(NeuronNetwork.totalError>Math.pow(10.0,-3))
		

		  
		//���涥�q
		println("-------------------------Executing-------------------------")
//        executingData.foreach((data:Array[Double])=> {
//          //println("<InputLayer>")
//          NeuronNetwork.execute(data)
//        })
	}
	
	def modifyLearnGain(curError:Double, preError:Double){
	  if (preError>0)
	  {
		  if (curError<preError) {
		    learnGain = learnGain*1.1
		    println("LearnGain become greater!!!" + learnGain)
		    }
		  else if (curError>preError){
		    learnGain = learnGain * 0.8
		    println("LearnGain become smaller!!!" + learnGain)
		    }
	  }
	}
	
}
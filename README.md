#README
### Intrucciones de Uso:


# Instrucciones de uso

#### Compilar:
```
mvn clean install
```
    
#### Ejecutar

 ```
java -jar .\target\criptoTp-jar-with-dependencies.jar
 ```
Parametros:

* Required:
  * -embed                              
  * Hide data
  * -extract                            
    * Extract data 
  * -in STRING[]                        
    * Data to be hidden
  * -out STRING[]                       
    * bmp output file to hide the data in (data + image)
  * -p STRING[]                         
    * Image in bmp format that will hide the data
  * -steg [LSB1 | LSB4 | LSBI] 
    * Steganography algorithm to be used <LSB1 | LSB4 | LSBI>
* Optionals:
  * -pass STRING[]
      * Encryption password
  * -a [AES128 | AES192 | AES256 | DES]
      * Encryption algorithm to be used <aes128 | aes192 | aes256 | des> (default:AES128)
  * -m [ECB | CFB | OFB | CBC]
      * Encryption mode <ecb | cfb | ofb | cbc> (default: CBC)



###### Ejemplo 1: Extraccion LSBI con encripción AES256 and OFB 
  ```
java -jar .\target\criptoTp-jar-with-dependencies.jar -extract -p ".\archivos\ladoLSBIaes256ofb.bmp" -out ".\archivos\result" -steg LSBI -pass secreto -a AES256 -m OFB
  ```
###### Ejemplo 2: Escondido con LSB1
  ```
java -jar .\target\criptoTp-jar-with-dependencies.jar -embed -in ".\archivos\itba.png" -p ".\archivos\lado.bmp" -out ".\archivos\lsb1.bmp" -steg LSB1 
  ```
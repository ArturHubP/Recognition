package com.pet.recognition

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.DrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.pet.recognition.facerecognizer.ProcessedImage
import com.pet.recognition.remote.api.ApiResponse
import com.pet.recognition.remote.dto.Transaction
import com.pet.recognition.ui.home.AddFaceViewModel
import com.pet.recognition.ui.home.ReceiverViewModel
import com.pet.recognition.ui.home.Scanner
import com.pet.recognition.ui.util.Screen
import com.pet.recognition.ui.util.beautifulDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    navigateToScanner:() ->Unit,
    viewModel: MainViewModel = hiltViewModel(),
    receiverViewModel: ReceiverViewModel = hiltViewModel()
){
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val balancAmount by viewModel.balanceAmount.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val transactionResponse by viewModel.transactionResponse.collectAsState()
    val processedImage by viewModel.image.collectAsState(initial = null)
    var receiverImage by remember{ mutableStateOf(ImageBitmap(50,50)) }
    val receiverUser by viewModel.receiverUser.collectAsState()
    var moneyForSend by remember{ mutableStateOf("0") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(transactionResponse){
        if(transactionResponse is ApiResponse.Success){
            Toast.makeText(context,"Деньги успешно доставлены",Toast.LENGTH_SHORT).show()
            viewModel.getBalance()
            sheetState.hide()
        }
    }
    LaunchedEffect(Unit){
        delay(1000)
        viewModel.getBalance()
    }
    if(processedImage!= null) {
        LaunchedEffect(processedImage) {
            receiverImage = processedImage!!
            viewModel.getReceiverUser()
            sheetState.show()
        }
    }



    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp),
        sheetContent = {
            Column (
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxWidth(),

                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ){
                    IconButton(onClick = {
                        scope.launch {
                            sheetState.hide()
                        }
                }){
                        Icon(imageVector = ImageVector.vectorResource(R.drawable.close), contentDescription = null)
                    }
                }
                Row (Modifier.padding(top = 16.dp, bottom = 8.dp)){
                    Column {
                        Image(
                            receiverImage,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .padding(10.dp)
                                .clip(
                                    CircleShape
                                )
                        )
                    }
                    Column {
                        Text(
                            text = "Имя: ${receiverUser.firstName}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF000000),
                                letterSpacing = 0.5.sp,
                            )
                        )
                        Text(
                            text = "Фамилия: ${receiverUser.lastName}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF000000),
                                letterSpacing = 0.5.sp,
                            )
                        )
                        Text(
                            text = "Email: ${receiverUser.email}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF000000),
                                letterSpacing = 0.5.sp,
                            )
                        )
                        Text(
                            text = "Пол: ${receiverUser.sex}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF000000),
                                letterSpacing = 0.5.sp,
                            )
                        )
                        Text(
                            text = "Дата рождения: ${beautifulDate(receiverUser.dateOfBirth)}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF000000),
                                letterSpacing = 0.5.sp,
                            )
                        )
                    }
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    value = moneyForSend,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { string -> if(moneyForSend.length <= 8)moneyForSend = string }
                )
                Row(
                    Modifier
                        .background(
                            color = if (moneyForSend.isNotEmpty() && moneyForSend.isDigitsOnly() && balancAmount >= moneyForSend.toInt())
                                Color(0xFFFFDD2D)
                            else Color(0xFFF18484),
                            shape = RoundedCornerShape(size = 7.dp)
                        )
                        .clip(RoundedCornerShape(size = 7.dp))
                        .clickable(
                            enabled = if (moneyForSend.isNotEmpty() && moneyForSend.isDigitsOnly()) balancAmount >= moneyForSend.toInt() else false,
                            onClick = {
                                viewModel.makeTransaction(
                                    transaction = Transaction(
                                        moneyForSend.toInt(),
                                        receiverUser.id
                                    )
                                )
                            }
                        )
                        .fillMaxWidth()
                        .height(60.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Перевести")
                }
            }
        }
    ){
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (topBar, cash, button) = createRefs()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(topBar) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ){

                    Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null,
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape))
                    Text(text = currentUser?.name?:"")
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = {

                            }
                        ){
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null,Modifier.weight(1f))
                        }
                    }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(92.dp)
                    .shadow(20.dp, shape = RoundedCornerShape(20.dp))
                    .constrainAs(cash) {
                        top.linkTo(topBar.bottom, 40.dp)
                        start.linkTo(parent.start, 20.dp)
                        end.linkTo(parent.end, 20.dp)
                    }
                    .background(Color.White)
                    .clip(RoundedCornerShape(20.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.group_2),
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 32.dp)

                )
                Column {
                    Text(text = "Баланс",
                        style = TextStyle(
                            fontSize = 22.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF000000),
                        )
                    )
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(text = balancAmount.toString(),
                            style = TextStyle(
                                fontSize = 20.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF000000),
                            )
                        )
                        Image(imageVector = ImageVector.vectorResource(R.drawable.ic_twotone_currency_ruble), contentDescription = null,Modifier.padding(start = 7.dp))

                    }

                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .constrainAs(button) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }){
                Scanner(navigateToScanner)
            }

        }
    }

}
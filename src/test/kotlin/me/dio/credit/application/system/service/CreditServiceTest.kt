import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.service.impl.CreditService
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.enummeration.Status
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.CustomerServiceTest
import me.dio.credit.application.system.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {

    @MockK lateinit var creditRepository: CreditRepository
    @InjectMockKs lateinit var creditService: CreditService
    @MockK lateinit var customerRepository: CustomerRepository
    @InjectMockKs lateinit var customerService: CustomerService

    @Test
    fun `should create credit`(){

        //given
        val fakeId: Long = Random().nextLong();
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId)} returns Optional.of(fakeCustomer)

        val fakeCredit :Credit = buildCredit(customer = fakeCustomer);
        every {creditRepository.save(any())} returns fakeCredit

        //when
        val actual: Credit = creditService.save(fakeCredit)

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) {creditRepository.save(fakeCredit)}
    }

    @Test
    fun `should find credit by creditCode`(){

        //given
        val fakeId: Long = Random().nextLong();
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId)} returns Optional.of(fakeCustomer)

        val fakeCredit: Credit = buildCredit(customer = fakeCustomer)
        every { creditRepository.findByCreditCode(fakeCredit.creditCode)} returns fakeCredit

        //when
        val actual: Credit = creditService.findByCreditCode(fakeId, fakeCredit.creditCode)

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isExactlyInstanceOf(Credit::class.java)
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1){creditRepository.findByCreditCode(fakeCredit.creditCode)}
    }

    @Test
    fun `should not find credit by id and throw Business Exception`()
    {
        val fakeCreditCode: UUID = UUID.randomUUID()
        val fakeId: Long = Random().nextLong();
        val fakeCustomer: Customer = CustomerServiceTest.buildCustomer(id = fakeId)
        val fakeCredit: Credit = buildCredit(customer = fakeCustomer)

        //given
        every { creditRepository.findByCreditCode(fakeCreditCode)} returns fakeCredit

        //when

        //then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
                .isThrownBy { creditService.findByCreditCode(fakeId, fakeCreditCode)}
                .withMessage("Id $fakeCreditCode not found")
        verify(exactly = 1){creditRepository.findByCreditCode(fakeCreditCode)}
    }

    companion object {
        fun buildCustomer(
                firstName: String = "Cami",
                lastName: String = "Cavalcante",
                cpf: String = "28475934625",
                email: String = "camila@gmail.com",
                password: String = "12345",
                zipCode: String = "12345",
                street: String = "Rua da Cami",
                income: BigDecimal = BigDecimal.valueOf(1000.0),
                id: Long = 1L
        ) = Customer(
                firstName = firstName,
                lastName = lastName,
                cpf = cpf,
                email = email,
                password = password,
                address = Address(
                        zipCode = zipCode,
                        street = street,
                ),
                income = income,
                id = id
        )

        fun buildCredit(
                creditCode: UUID = UUID.randomUUID(),
                creditValue: BigDecimal = BigDecimal(1000),
                numberOfInstallments:Int = 10,
                customer : Customer
        ) = Credit(
                creditCode = creditCode,
                creditValue = creditValue,
                dayFirstInstallment = LocalDate.now(),
                numberOfInstallments = numberOfInstallments,
                status = Status.IN_PROGRESS,
                customer = customer
        )

    }

}
.globl main
main:
    pushq %rbp
    movq %rsp, %rbp
    subq $32, %rsp
    movl $1, %eax
    movl %eax, -4(%rbp)
    movl $2, %eax
    movl %eax, -8(%rbp)
    movl $1, %eax
    movl %eax, -12(%rbp)
L0:
    cmpl $0, -12(%rbp)
    je L1
    movl $3, %eax
    imull $2, %eax
    movl %eax, -16(%rbp)
    movl -16(%rbp), %eax
    imull -8(%rbp), %eax
    movl %eax, -20(%rbp)
    movl -4(%rbp), %eax
    addl -20(%rbp), %eax
    movl %eax, -24(%rbp)
    movl -24(%rbp), %eax
    movl %eax, -4(%rbp)
    movl $0, %eax
    movl %eax, -12(%rbp)
    jmp L0
L1:
    movl -4(%rbp), %eax
    jmp .L_return
.L_return:
    leave
    ret

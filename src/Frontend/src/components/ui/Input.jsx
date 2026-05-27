import { forwardRef, useState } from 'react'
import { cn } from '../../utils/cn'

export const Input = forwardRef(function Input(
  { label, error, className, type = 'text', value, defaultValue, onChange, onFocus, onBlur, ...rest },
  ref
) {
  const [focused, setFocused] = useState(false)
  const [internal, setInternal] = useState(defaultValue ?? '')
  const v = value !== undefined ? value : internal
  const hasValue = !!(v && String(v).length)
  const isFloating = focused || hasValue

  return (
    <div className={cn('relative w-full', error && 'animate-shake')}>
      {label && (
        <label
          className={cn(
            'pointer-events-none absolute left-4 transition-all duration-base ease-out',
            isFloating
              ? 'top-1 text-[10px] tracking-[0.08em] uppercase text-gray-600'
              : 'top-1/2 -translate-y-1/2 text-sm text-gray-600',
            error && 'text-priority-high'
          )}
        >
          {label}
        </label>
      )}
      <input
        ref={ref}
        type={type}
        value={value}
        defaultValue={defaultValue}
        onChange={(e) => { if (value === undefined) setInternal(e.target.value); onChange?.(e) }}
        onFocus={(e) => { setFocused(true); onFocus?.(e) }}
        onBlur={(e) => { setFocused(false); onBlur?.(e) }}
        className={cn(
          'w-full h-[52px] px-4 bg-paper rounded-md border outline-none transition-all duration-base',
          'text-base text-ink placeholder:text-gray-400',
          label && 'pt-5 pb-1',
          'border-gray-200 focus:border-ink focus:ring-[3px] focus:ring-ink/5',
          error && 'border-priority-high focus:border-priority-high focus:ring-priority-high/10',
          className
        )}
        {...rest}
      />
      {error && (
        <div className="mt-1 text-xs text-priority-high">{error}</div>
      )}
    </div>
  )
})
